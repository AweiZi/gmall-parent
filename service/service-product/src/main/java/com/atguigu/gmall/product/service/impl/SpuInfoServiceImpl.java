package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Awei
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-22 19:16:05
 */
@Service

public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {
    @Autowired
    SpuInfoMapper spuInfoMapper;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuSaleAttrValueService saleAttrValueService;

    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
//        1.把spu基本信息保存到spu_info中
        spuInfoMapper.insert(spuInfo);
        Long id = spuInfo.getId();
//        2.把spu图片保存到spu_image
        List<SpuImage> imageList = spuInfo.getSpuImageList();
        for (SpuImage image : imageList) {
//            回填spuid
            image.setSpuId(id);
        }
        spuImageService.saveBatch(imageList);
//        3.保存销售属性名到spu_sale_attr
        List<SpuSaleAttr> attrNameList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr attr : attrNameList) {
//            回填spuid
            attr.setSpuId(id);
//            销售属性值
            List<SpuSaleAttrValue> valueList = attr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
//                回填spu_id
                value.setSpuId(id);
                String saleAttrName = attr.getSaleAttrName();
//                回填销售属性名
                value.setSaleAttrValueName(saleAttrName);
//
            }
//            保存销售属性值
            saleAttrValueService.saveBatch(valueList);
        }
//        保存到数据库
        spuSaleAttrService.saveBatch(attrNameList);
    }

    @Override
    public Page<SpuInfo> getPageSize(Page<SpuInfo> page, Long category3Id) {
        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id, category3Id);
        Page<SpuInfo> spuInfoPage = spuInfoMapper.selectPage(page, queryWrapper);
        return spuInfoPage;
    }
}




