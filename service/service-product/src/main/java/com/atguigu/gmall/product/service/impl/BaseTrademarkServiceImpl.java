package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Awei
 * @description 针对表【base_trademark(品牌表)】的数据库操作Service实现
 * @createDate 2022-08-22 19:16:05
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark>
        implements BaseTrademarkService {
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> list = baseTrademarkMapper.selectList(null);
        return list;
    }
}




