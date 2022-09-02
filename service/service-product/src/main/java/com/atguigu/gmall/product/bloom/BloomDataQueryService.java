package com.atguigu.gmall.product.bloom;

import java.util.List;

/**
 * 布隆数据查询服务
 */
public interface BloomDataQueryService {
    /**
     * 模板模式
     * 父类规定算法，子类写实现方法
     * 所有的设计模式，封装继承多态
     * @return
     */
    List queryData();
}
