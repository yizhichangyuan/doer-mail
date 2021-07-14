package com.lookstarry.doermail.authserver;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * SpringSession核心原理
 * 1、@EnableSpringHttpSession 自动导入了配置RedisHttpSessionConfiguration
 *      1）给容器中添加了一个组件
 *          SessionRepository =》RedisOperationsSessionRepository：redis操作session，session的增删改查封装类
 *      2）SessionRepositoryFilter：session存储过滤器，每个请求过来都必须经过Filter
 *         1、构造函数创建的时候，会自动从容器中获取到SessionRepository
 *         2、原生的Request和Response对象都被包装成SessionRepositoryRequestWrapper和SessionRepositoryResponseWrapper
 *         3、以后获取session，从request.getSession()原始操作变为了wrappedRequest.getSession() =》从sessionRepository中获取到
 *          =》RedisOperationsSessionRepository获取
 *  原理：装饰者模式
 *  session存在Redis的数据，浏览器不管不断刷新会自动延期，redis中的数据是有过期时间的，会话关闭会清除，完全利用Redis实现了Session功能
 */
@EnableRedisHttpSession // 整合SpringSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class DoermallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermallAuthServerApplication.class, args);
    }

}
