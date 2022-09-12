package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignInterceptorConfiguration {
    /**
     * 把用户id带到feign即将发起的新请求中
     */
    @Bean
    public RequestInterceptor userHeaderInterceptor(){
        return (template)->{
            //修改请求模板
            System.out.println("真帅气啊");
            //即时调用，获取老请求
            //获取当前线程
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //获取老请求的请求头
            HttpServletRequest request = attributes.getRequest();
            //得到老用户的用户id
            String userId = request.getHeader(SysRedisConst.USERID_HEADER);
            //将用户id添加到feign的新请求中
            template.header(SysRedisConst.USERID_HEADER,userId);

            //临时id也透传
            String tempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);

            template.header(SysRedisConst.USERID_HEADER,tempId);
        };
    }
}
