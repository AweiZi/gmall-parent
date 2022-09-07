package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

public interface GoodsService {
    /**
     *将商品信息保存到es
     * @param goods
     */
    void saveGoods(Goods goods);

    /**
     * 从es删除对应的商品
     * @param skuId
     */
    void deleteGoods(Long skuId);

    /**
     * 根据查询条件查询对应商品
     * @param paramVo
     * @return
     */
    SearchResponseVo search(SearchParamVo paramVo);

    void updateHotScore(Long skuId, Long score);
}
