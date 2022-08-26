package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Spu功能")
@RestController
@RequestMapping("admin/product")
public class SpuController {
    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    private SpuImageService spuImageService;



    //    http://api.gmall.com/admin/product/1/10?category3Id=61
    @GetMapping("{pageNub}/{pageSize}")
    public Result getCategory(@PathVariable Long pageNub,
                              @PathVariable Long pageSize,
                              @RequestParam("category3Id") Long category3Id) {
        Page<SpuInfo> spuInfoPage = new Page<>(pageNub, pageSize);
        Page<SpuInfo> page = spuInfoService.page(spuInfoPage);

        Page<SpuInfo> spuInfo = spuInfoService.getPageSize(page, category3Id);

        return Result.ok(spuInfo);
    }

    //        TODO
    @Autowired
    BaseTrademarkService baseTrademarkService;

    /*
     * 获取品牌属性
     *http://192.168.6.1/admin/product/baseTrademark/getTrademarkList
     * */
    @ApiOperation(value = "获取品牌属性")
    @GetMapping("baseTrademark/getTrademarkList")
    public Result getTrademarkList() {
        List<BaseTrademark> list = baseTrademarkService.getTrademarkList();
        return Result.ok(list);
    }

    /*
     *保存spu信息
     * http://192.168.6.1/admin/product/saveSpuInfo
     * */
    @ApiOperation(value = "保存spu信息")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /*查询spu的所有图片
     *     http://api.gmall.com/admin/product/spuImageList/{spuId}
     * */
    @ApiOperation(value = "根据spuId获取图片列表")
    @GetMapping("spuImageList/{spuId}")
    public Result getIdImages(@PathVariable Long spuId) {
        List<SpuImage> imageList = spuImageService.list(new LambdaQueryWrapper<SpuImage>()
                .eq(SpuImage::getSpuId, spuId));
        return Result.ok(imageList);
    }

}
