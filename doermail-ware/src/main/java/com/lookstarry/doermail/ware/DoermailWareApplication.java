package com.lookstarry.doermail.ware;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@EnableFeignClients(basePackages = "com.lookstarry.doermail.ware.feign")
@MapperScan(basePackages = "com.lookstarry.doermail.ware.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class DoermailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailWareApplication.class, args);
    }

}
