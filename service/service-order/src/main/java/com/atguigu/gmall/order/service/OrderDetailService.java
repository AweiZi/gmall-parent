package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Awei
 * @description 针对表【order_detail(订单明细表)】的数据库操作Service
 * @createDate 2022-09-12 11:11:16
 */
public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 查询某个订单明细
     * @param orderId
     * @param userId
     *
     * @return
     */
    List<OrderDetail> getOrderDetails(Long orderId, Long userId);
}
