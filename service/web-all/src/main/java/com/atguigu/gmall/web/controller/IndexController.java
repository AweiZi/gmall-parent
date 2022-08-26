package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.web.feign.CategoryFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;

    /**
     * 跳转首页
     * @return
     */
    @GetMapping({"/index","/"})
    public String indexPage(Model model){
//       远程查询所有菜单并封装成树形结构的模型
        Result<List<CategoryTreeTo>> result = categoryFeignClient.getAllCategoryWithTree();
        if (result.isOk()){
//            远程成功
            List<CategoryTreeTo> data = result.getData();
            model.addAttribute("list",data);
        }

        return "index/index.html";//页面的逻辑视图名
    }
}
