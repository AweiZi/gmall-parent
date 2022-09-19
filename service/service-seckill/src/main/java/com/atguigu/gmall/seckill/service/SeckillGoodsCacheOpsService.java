package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

public interface SeckillGoodsCacheOpsService {
    /**
     * 将秒杀商品设置到redis'及本地缓存
     * @param list
     */
    void upSeckillGoods(List<SeckillGoods> list);

    /**
     * 清除本地缓存
     */
    void clearCache();

    /**
     * 获取秒杀商品
     * @return
     */
    List<SeckillGoods> getSeckillGoodsFromLocal();

    /**
     * 从redis获取秒杀商品
     * @return
     */
    List<SeckillGoods> getSeckillGoodsFromRemote();

    /**
     * 本地与redis同步缓存
     */
    void syncLocalAndRedisCache();

    SeckillGoods getSeckillGoodsDetail(Long skuId);
}
