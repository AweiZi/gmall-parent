package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
* @author Awei
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-22 19:16:05
*/
public interface SkuInfoService extends IService<SkuInfo> {

    void saveSkuInfo(SkuInfo info);

    void cancelSale(Long skuId);

    void onSale(Long skuId);

    /*
    * 获取sku商品详情数据
    * */
    SkuDetailTo getSkuDetail(Long skuId);
    /**
     * 获取sku的实时价格
     * @param skuId
     * @return
     */
    BigDecimal get1010Price(Long skuId);
}
