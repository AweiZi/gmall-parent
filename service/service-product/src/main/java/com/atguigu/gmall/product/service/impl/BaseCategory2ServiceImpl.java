package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Awei
 * @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
 * @createDate 2022-08-22 19:16:04
 */
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
        implements BaseCategory2Service {
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Override
    public List<BaseCategory2> getCategory1Child(String c1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id,c1Id);

        List<BaseCategory2> list = baseCategory2Mapper.selectList(wrapper);
        return list;
    }

    @Override
    public List<CategoryTreeTo> getAllCategoryWithTree() {

        return baseCategory2Mapper.getAllCategoryWithTree();
    }
}




