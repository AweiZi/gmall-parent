package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/product")
public class SkuController {
    @Autowired
    private SkuInfoService skuInfoService;


    /*
     *http://api.gmall.com/admin/product/list/{page}/{limit}
     * */
    @ApiOperation(value = "获取sku分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result getSkuList(@PathVariable Long page,
                             @PathVariable Long limit) {
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        Page<SkuInfo> infoPage = skuInfoService.page(skuInfoPage);
        return Result.ok(infoPage);
    }

    /*
     * 接口	http://api.gmall.com/admin/product/saveSkuInfo
     * */
    @ApiOperation(value = "添加sku")
    @PostMapping("saveSkuInfo")
    public Result save(@RequestBody SkuInfo info) {
//        sku的大保存
        skuInfoService.saveSkuInfo(info);

        return Result.ok();
    }


    /*
     * http://api.gmall.com/admin/product/onSale/{skuId}
     * */
    @ApiOperation(value = "sku上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }

    /**
     * 商品下架
     *
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId")Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }
}
