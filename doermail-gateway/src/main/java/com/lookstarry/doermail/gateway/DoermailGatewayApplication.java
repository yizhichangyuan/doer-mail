package com.lookstarry.doermail.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、开启服务注册发现，这样才知道请求路由其他服务知道其他服务在哪
 * （配置Nacos中心地址）
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //因为继承common模块依赖有mybatis，排除数据源有关配置，否则项目启动要求插入数据源
public class DoermailGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailGatewayApplication.class, args);
    }

}
