package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Test
    void saveTest(){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("hello","world");
        System.out.println("保存完成");
        System.out.println("ops.get(\"hello\") = " + ops.get("hello"));
    }
}
