package com.atguigu.starter.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GmallCache {
    String cacheKey() default "";

    String bloomName() default "";//如果指定了布隆过滤器的名字就用，如果没有就不用

    String bloomValue() default "";//指定布隆过滤器如果需要判定的话，用什么表达式计算出的值进行计算

    String lockName() default "lock:global";//传入精确锁就用精确锁，否则用全局

    long ttl() default 60*30L;
}
