package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.model.vo.order.OrderWareMapVo;
import com.atguigu.gmall.model.vo.order.WareChildOrderVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("拆单")
@RestController
@Slf4j
@RequestMapping("/api/order")
public class OrderSpiltController {
    @Autowired
    OrderBizService orderBizService;

    @PostMapping("/orderSplit")
    public List<WareChildOrderVo> orderSplit(OrderWareMapVo params){
        log.info("订单执行拆单:{}",params);
        //将大订单拆成子订单保存数据库
        return orderBizService.orderSplit(params);
    }

}
