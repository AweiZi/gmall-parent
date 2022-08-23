package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("admin/product")
public class CategoryController {
    @Autowired
    private BaseCategory1Service baseCategory1Service;
    @Autowired
    private BaseCategory2Service baseCategory2Service;
    @Autowired
    private BaseCategory3Service baseCategory3Service;


//    /admin/product/getCategory1
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> list = baseCategory1Service.list();

        return Result.ok(list);
    }

//    http://192.168.6.1/admin/product/getCategory2/6
    @GetMapping("getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable String c1Id){
        List<BaseCategory2> list= baseCategory2Service.getCategory1Child(c1Id);
        return Result.ok(list);
    }
//    http://192.168.6.1/admin/product/getCategory3/46
    @GetMapping("getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable String c2Id){
       List<BaseCategory3> list= baseCategory3Service.getCategory2Child(c2Id);
        return Result.ok(list);
    }



}
