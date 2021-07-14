package com.lookstarry.doermail.cart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoermallCartApplicationTests {
    @Autowired
    StringRedisTemplate redisTemplate;

//    @Test
//    void contextLoads() {
//    }

    @Test
    public void testCart(){
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps("temp");
        System.out.println(operations.get("a"));
    }

}
