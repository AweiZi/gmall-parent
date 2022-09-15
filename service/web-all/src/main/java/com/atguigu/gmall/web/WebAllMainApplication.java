package com.atguigu.gmall.web;

import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableDiscoveryClient
//@EnableCircuitBreaker
@EnableAutoFeignInterceptor
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign"

}) //只会扫描主程序所在的子包
@SpringCloudApplication
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class, args);
    }
}

