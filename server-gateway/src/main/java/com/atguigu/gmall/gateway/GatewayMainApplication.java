package com.atguigu.gmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;


//@SpringBootApplication
//@EnableDiscoveryClient开启服务发现【1.
//@EnableCircuitBreaker开启熔断降级【1、导入jar，2.使用这个注册
@SpringCloudApplication
public class GatewayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class,args);
    }
}


