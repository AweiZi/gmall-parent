package com.atguigu.gmall.order.service.impl;


import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.order.mapper.CartInfoMapper;
import com.atguigu.gmall.order.service.CartInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author Awei
* @description 针对表【cart_info(购物车表 用户登录系统时更新冗余)】的数据库操作Service实现
* @createDate 2022-09-12 11:11:16
*/
@Service
public class CartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo>
    implements CartInfoService {

}




