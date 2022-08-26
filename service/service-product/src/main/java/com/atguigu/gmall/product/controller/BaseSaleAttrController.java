package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("admin/product")
public class BaseSaleAttrController {
    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    @Autowired
    private SpuSaleAttrService saleAttrService;

    //    http://192.168.6.1/admin/product/baseSaleAttrList
    @ApiOperation(value = "查询所有销售属性")
    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList() {
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }

    /*查询指定spu当时定义的所有销售属性名和值
     * http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
     *
     * */
    @ApiOperation(value = "根据spuId获取销售属性")
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttrValue> listSaleAttr = saleAttrService.getSaleAttrAndValueBySpuId(spuId);
        return Result.ok(listSaleAttr);
    }
}
