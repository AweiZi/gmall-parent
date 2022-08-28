package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Awei
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {
    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Autowired
    SkuImageMapper skuImageMapper;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo info) {
        //        1.sku的基本信息保存到sku_info
        save(info);
        Long skuId = info.getId();
        //        2.sku的图片信息保存到sku_image
        for (SkuImage skuImage : info.getSkuImageList()) {
            skuImage.setSkuId(skuId);
        }
        skuImageService.saveBatch(info.getSkuImageList());
        // 3.sku的平台属性和值的关系保存到sku_attr_value
        List<SkuAttrValue> attrValueList = info.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValueList) {
            attrValue.setSkuId(skuId);
        }
        skuAttrValueService.saveBatch(attrValueList);
        // 4.sku的销售属性名和值的关系保存到sku_sale_value
        List<SkuSaleAttrValue> saleAttrValueList = info.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : saleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(info.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(saleAttrValueList);

    }

    @Override
    public void cancelSale(Long skuId) {
        //1、改数据库 sku_info 这个skuId的is_sale； 1上架  0下架
        skuInfoMapper.updateIsSale(skuId, 0);
        //TODO 2、从es中删除这个商品
    }

    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.updateIsSale(skuId, 1);
        //TODO 2、给es中保存这个商品，商品就能被检索到了

    }

    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        SkuDetailTo detailTo = new SkuDetailTo();
        //        查询skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        //        商品sku的基本信息【价格、重量、名字...】   sku_info
        //        将查询到的数据放到 SkuDetailTo 中
        detailTo.setSkuInfo(skuInfo);
        //        查询商品的sku的图片
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(imageList);

        //        商品所属sku信息的完整分类
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        detailTo.setCategoryView(categoryViewTo);
        //        实时价格查询
        BigDecimal price = get1010Price(skuId);
        detailTo.setPrice(price);
        //商品sku所属的spu当时定义的所有销售属性名值组合 （固定好顺序）
        // spu_sale_attr、spu_sale_attr_value
        //并标识出当前sku到底是spu的哪种组合，页面要有高亮框sku_sale_attr_value
        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(), skuId);
        detailTo.setSpuSaleAttrList(saleAttrList);    //5、商品（sku）类似推荐    （x）
        //商品sku的所有兄弟产品的销售属性名和值组合关系全部查出来
        //{"118|120":"50","119|121":"50"}
        Long id = skuInfo.getId();//118|120
        Long spuId = skuInfo.getSpuId();//50
        String valueJson =spuSaleAttrService.getAllSkuSaleAttrValueJson(spuId);
        detailTo.setValuesSkuJson(valueJson);
        //6、商品（sku）介绍[所属的spu的海报]        spu_poster（x）
        //7、商品（sku）的规格参数                  sku_attr_value
        //8、商品（sku）售后、评论...              相关的表 (x)
        return detailTo;
    }

    @Override
    public BigDecimal get1010Price(Long skuId) {
        //性能低下
        BigDecimal price = skuInfoMapper.getRealPrice(skuId);
        return price;
    }

    @Override
    public SkuInfo getDetailSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    @Override
    public List<SkuImage> getDetailSkuImages(Long skuId) {
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);

        return imageList;
    }
}




