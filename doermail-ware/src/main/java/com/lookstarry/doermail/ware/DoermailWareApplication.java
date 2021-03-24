package com.lookstarry.doermail.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DoermailWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailWareApplication.class, args);
    }

}
