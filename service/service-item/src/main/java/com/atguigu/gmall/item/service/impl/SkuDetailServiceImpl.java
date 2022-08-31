package com.atguigu.gmall.item.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.item.feigh.SkuDetailFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    SkuDetailFeignClient skuDetailFeignClient;

    /**
     * 可配置的线程池，可自动注入
     */
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CacheOpsService cacheOpsService;
    ReentrantLock lock = new ReentrantLock();

    //未缓存
    public SkuDetailTo getSkuDetailRpc(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
        //查基本信息
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = skuDetailFeignClient.getSkuInfo(skuId);
            SkuInfo skuInfo = result.getData();
            detailTo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);

        //2.查商品图片信息
        CompletableFuture<Void> imageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SkuImage>> skuImages = skuDetailFeignClient.getSkuImages(skuId);
            skuInfo.setSkuImageList(skuImages.getData());
        }, executor);

        //3.查商品实时价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> price = skuDetailFeignClient.getSku1010Price(skuId);
            detailTo.setPrice(price.getData());
        }, executor);
        //4.查销售属性名值
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Long spuId = skuInfo.getSpuId();
            Result<List<SpuSaleAttr>> saleattrvalues = skuDetailFeignClient.getSkuSaleattrvalues(skuId, spuId);
            detailTo.setSpuSaleAttrList(saleattrvalues.getData());
        }, executor);
        //5.查sku组合
        CompletableFuture<Void> skuVlaueFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<String> sKuValueJson = skuDetailFeignClient.getSKuValueJson(skuId);
            detailTo.setValuesSkuJson(sKuValueJson.getData());
        }, executor);

        //6.查分类
        CompletableFuture<Void> categoryFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> categoryView = skuDetailFeignClient.getCategoryView(skuInfo.getCategory3Id());
            detailTo.setCategoryView(categoryView.getData());
        }, executor);


        CompletableFuture
                .allOf(imageFuture, priceFuture, saleAttrFuture, skuVlaueFuture, categoryFuture)
                .join();
        return detailTo;
    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_PREFIX + skuId;
        //1、先查缓存
        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        //2.判断
        if (cacheData == null) {
            //3.缓存没有
            //4.先问布隆，是否有这个商品
            boolean contain = cacheOpsService.bloomContains(skuId);
            if (!contain) {
                //5.布隆说没有。一定没有
                log.info("[{}]商品 - 布龙说没有，检测到隐藏的攻击风险。。。", +skuId);
                return null;
            }
            //6.布隆说有，有可能有，就需要回源查数据
            boolean lock = cacheOpsService.tryLock(skuId);
            if (lock) {
                //7.获取锁成功，查询远程
                log.info("[{}]商品  缓存未命中，布龙说有，准备回源。。。", skuId);
                SkuDetailTo fromRpc = getSkuDetailRpc(skuId);
                //8.数据放缓存
                cacheOpsService.saveData(cacheKey, fromRpc);
                //9.解锁
                cacheOpsService.unLock(skuId);
                return fromRpc;
            }
            //10.没获取到锁
            try {
                Thread.sleep(1000);
                return cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return cacheData;
    }

    public SkuDetailTo getSkuDetailXxxxFeature(Long skuId) {
        //每个不同的sku，用自己专用的锁
        //看缓存中有没有 sku:info49
        String jsonStr = redisTemplate.opsForValue().get("sku:info:" + skuId);

        if ("x".equals(jsonStr)) {
            //说明以前查过，只不过数据库没有此纪录，为了避免再次回源，缓存一个占位符
            return null;
        }
        if (StringUtils.isEmpty(jsonStr)) {
            //2.redis没有缓存数据
            //2.1回源。之前可以判断redis中保存的sku的id集合，有没有这个id
            //防止随机值穿透攻击?回源之前，先要用布隆/bitmap判断有没有
            SkuDetailTo fromRpc = null;
            fromRpc = getSkuDetailRpc(skuId);
            //2.2放入缓存【查找的对象转为json字符串保存到redis】
            String cacheJson = "x";
            if (fromRpc != null) {
                cacheJson = Jsons.toStr(fromRpc);
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 7, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, cacheJson, 30, TimeUnit.MINUTES);
            }
            return fromRpc;
        }
        //3.缓存中有，把json转成指定的对象
        SkuDetailTo skuDetailTo = Jsons.toObj(jsonStr, SkuDetailTo.class);
        return skuDetailTo;
    }


    //map作为缓存{本地缓存，}优缺点
    //        private Map<Long,SkuDetailTo> skuCache = new ConcurrentHashMap<>();
    //        @Override
    //        public SkuDetailTo getSkuDetail(Long skuId){
    //            SkuDetailTo cacheData = skuCache.get(skuId);
    //            if (cacheData==null){
    //                //3.没有缓存,真正查询【回源】（回到数据源头真正检索）
    //                //缓存命中率提高到100%预缓存机制
    //                SkuDetailTo fromRpc = getSkuDetailRpc(skuId);
    //                skuCache.put(skuId,fromRpc);
    //                return fromRpc;
    //            }
    //            //4.缓存有
    //            return cacheData;
    //        }
}
