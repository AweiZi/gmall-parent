package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
//远程调用之前feign会自己找nacos要到service-product
public interface CategoryFeignClient {
    //
//    2.拿到远程的响应json转成Result类型的对象，并且返回的数据时List<CategoryTreeTo>
    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getAllCategoryWithTree();
}
