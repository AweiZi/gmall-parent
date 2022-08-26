package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "品牌")
@RestController
@RequestMapping("admin/product")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;
    /*
    分页查询所有品牌
    */
//    接口	http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}
    @ApiOperation(value = "分页查询所有品牌")
    @GetMapping("baseTrademark/{pn}/{size}")
    public Result baseTrademark(@PathVariable Long pn, @PathVariable Long size) {
        Page<BaseTrademark> page = new Page<>(pn, size);
//long current, long size
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page);

        return Result.ok(pageResult);
    }

    /*
     * 根据品牌id获取品牌信息
     * */
    @ApiOperation(value = "获取BaseTreademark")
    @GetMapping("baseTrademark/get/{id}")
    public Result getBaseTreademark(@PathVariable Long id) {
        BaseTrademark trademark = baseTrademarkService.getById(id);

        return Result.ok(trademark);
    }

    /*
     *
     * 修改品牌
     * */
    @ApiOperation(value = "修改BaseTrademark")
    @PutMapping("baseTrademark/update")
    public Result updatebaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    /*
     * 保存品牌
     * */
    @ApiOperation(value = "新增BaseTrademark")
    @PostMapping("baseTrademark/save")
    public Result savebaseTrademark(@RequestBody BaseTrademark trademark) {
        baseTrademarkService.save(trademark);
        return Result.ok();
    }

    /*
     * baseTrademark/remove/{id}
     * 删除品牌
     * */
    @DeleteMapping("baseTrademark/remove/{tid}")
    public Result deletebaseTrademark(@PathVariable Long tid) {
        baseTrademarkService.removeById(tid);
        return Result.ok();
    }


}
