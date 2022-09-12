package com.atguigu.gmall.cart;


import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
@EnableAutoExceptionHandler
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
@EnableAutoFeignInterceptor
@EnableThreadPool
public class CartMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartMainApplication.class,args);
    }
}
