package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Awei
* @description 针对表【base_category3(三级分类表)】的数据库操作Service
* @createDate 2022-08-22 19:16:05
*/
public interface BaseCategory3Service extends IService<BaseCategory3> {
    /**
     * 获取某个二级分类下的所有三级分类
     * @param c2Id
     * @return
     */
    List<BaseCategory3> getCategory2Child(String c2Id);

    /**
     * 根据三级分类id，查询整个精确路径
     * @param c3Id
     * @return
     */
    CategoryViewTo getCategoryView(Long c3Id);
}
