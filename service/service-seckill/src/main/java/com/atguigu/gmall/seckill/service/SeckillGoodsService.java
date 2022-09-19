package com.atguigu.gmall.seckill.service;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Awei
* @description 针对表【seckill_goods】的数据库操作Service
* @createDate 2022-09-19 19:22:11
*/
public interface SeckillGoodsService extends IService<SeckillGoods> {
    /**
     * 查询当天所有的秒杀商品
     * @return
     */
    List<SeckillGoods> getCurrentDaySeckillGoodsCache();

    SeckillGoods getSeckillGoodDetail(Long skuId);

    List<SeckillGoods> getCurrentDaySeckillGoodsList();

    /**
     * 扣减数据库库存
     * @param skuId
     */
    void deduceSeckillGoods(Long skuId);
}
