package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;

    /**
     *
     */
    @Override
    public void rebuildBoolm(String bloomName, BloomDataQueryService dataQueryService) {
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);
        //1.准备一个新的布隆过滤器，所有东西都准备好
        String newBloomName = bloomName + "_new";
        RBloomFilter<Object> newBloomFilter = redissonClient.getBloomFilter(newBloomName);
        //2.拿到所有商品id
//List<Long> allSkuId = skuInfoService.findAllSkuId();
        List list = dataQueryService.queryData();//动态决定
        //3.初始化新的布隆
        newBloomFilter.tryInit(5000000, 0.00001);

        for (Object skuId : list) {
            newBloomFilter.add(skuId);
        }
        //4.新布隆准备就绪

        //5.两个交换  大数据量的数据删除会导致redis卡死
        //TODO lua脚本
        oldBloomFilter.rename("bbbb_bloom");//老布隆下线
        newBloomFilter.rename(bloomName);//新布隆上线

        //6.删除老布隆和交换层
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("bbbb_bloom").deleteAsync();

    }
}
