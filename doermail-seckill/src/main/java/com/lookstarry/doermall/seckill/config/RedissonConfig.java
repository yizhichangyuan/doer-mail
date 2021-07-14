package com.lookstarry.doermall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @PackageName:com.lookstarry.doermall.seckill.config
 * @NAME:RedissonConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/3 16:52
 */
@Configuration
public class RedissonConfig {
    @Bean(destroyMethod="shutdown")
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.2.200:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
