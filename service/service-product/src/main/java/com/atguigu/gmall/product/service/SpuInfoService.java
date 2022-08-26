package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Awei
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-22 19:16:05
*/
public interface SpuInfoService extends IService<SpuInfo> {


    Page<SpuInfo> getPageSize(Page<SpuInfo> page, Long category3Id);

    void saveSpuInfo(SpuInfo spuInfo);

}
