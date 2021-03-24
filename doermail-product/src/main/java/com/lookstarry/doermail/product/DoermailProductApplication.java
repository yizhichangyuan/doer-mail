package com.lookstarry.doermail.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.lookstarry.doermail.product.dao")
public class DoermailProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailProductApplication.class, args);
    }

}
