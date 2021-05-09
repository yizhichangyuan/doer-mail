package com.lookstarry.doermail.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @PackageName:com.lookstarry.doermail.product.config
 * @NAME:MyRedissonConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/21 11:22
 */
@Configuration
public class MyRedissonConfig {
    /**
     * 所有对Redissoon的使用功能都是通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.2.200:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
