package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration//告诉springboot这是一个配置类
public class MybatisPlusConfig {
//   把mybatisplus的插件主题放到通过其
    @Bean
    public MybatisPlusInterceptor interceptor(){
//        插件主体
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        加入内部小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true);
//        分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
