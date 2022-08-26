package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Awei
 */
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    List<SpuSaleAttrValue> getSaleAttrAndValueBySpuId(Long spuId);

    /*
     *根据spu查询对应的spu对应的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
     * */
    List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId);
}
