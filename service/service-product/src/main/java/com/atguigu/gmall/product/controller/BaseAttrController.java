package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "spu商品相关")
@RestController
@RequestMapping("admin/product")
public class BaseAttrController {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;
    @Autowired
    private BaseAttrValueService baseAttrValueService;


    //    http://api.gmall.com/admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
    @ApiModelProperty(value = "根据相关id遍历")
    @GetMapping("attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result attrInfoList(@PathVariable String c1Id,
                               @PathVariable String c2Id,
                               @PathVariable String c3Id) {
        List<BaseAttrInfo> list = baseAttrInfoService.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);

        return Result.ok(list);
    }

    /**/
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo info) {
//            平台属性的新增
    baseAttrInfoService.saveAttrInfo(info);
        return Result.ok();
    }

    //    接口	http://api.gmall.com/admin/product/getAttrValueList/{attrId}
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId) {
        List<BaseAttrValue> list = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(list);
    }


}
