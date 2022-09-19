package com.atguigu.gmall.seckill.service.impl;


import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheOpsService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author Awei
* @description 针对表【seckill_goods】的数据库操作Service实现
* @createDate 2022-09-19 19:22:11
*/
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService {
    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    SeckillGoodsCacheOpsService cacheOpsService;


    @Override
    public List<SeckillGoods> getCurrentDaySeckillGoodsCache() {
        String date = DateUtil.formatDate(new Date());

        return seckillGoodsMapper.getSeckillGoodsByDate(date);
    }

    @Override
    public SeckillGoods getSeckillGoodDetail(Long skuId) {
        return cacheOpsService.getSeckillGoodsDetail(skuId);
    }
    /**
     * 从缓存中获取当天秒杀的所有商品
     * @return
     */
    @Override
    public List<SeckillGoods> getCurrentDaySeckillGoodsList() {
        return cacheOpsService.getSeckillGoodsFromLocal();
    }

    @Override
    public void deduceSeckillGoods(Long skuId) {
        seckillGoodsMapper.updateStockCount(skuId);

    }
}




