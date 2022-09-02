package com.atguigu.gmall.item;


import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Import(RedissonAutoConfiguration.class)
@EnableThreadPool
@EnableFeignClients
@SpringCloudApplication
@EnableAspectJAutoProxy//开启aspect（aop// ）的自动代理功能
public class ItemMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class,args);
    }
}
