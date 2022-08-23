package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Awei
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-08-22 19:16:05
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(String c1Id, String c2Id, String c3Id);
/*
* 保存平台属性
* */
    void saveAttrInfo(BaseAttrInfo info);



}
