package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Awei
* @description 针对表【sku_image(库存单元图片表)】的数据库操作Service
* @createDate 2022-08-22 19:16:04
*/
public interface SkuImageService extends IService<SkuImage> {

    /*
    * 查出当前sku的所有图片
    * */
    List<SkuImage> getSkuImage(Long skuId);
}
