package com.lookstarry.doermail.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DoermailThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailThirdPartyApplication.class, args);
    }
}
