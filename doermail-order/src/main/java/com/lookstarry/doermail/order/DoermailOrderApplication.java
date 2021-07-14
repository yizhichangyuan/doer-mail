package com.lookstarry.doermail.order;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用RabbitMQ
 * 1、引入amqp场景，RabiitAutoConfiguration就会自动生效
 * 2、给容器中自动配置了
 *       RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessageTemplate
 * 3、开启注解 @EnableRabbit
 * 4、配置spring-rabbit信息
 * 5、监听消息，启动类必须有@EnableRabbit
 *      @RabbitListener：标记在类或方法上
 *      @RabbitHandler：标在方法上
 *
 *  同一个对象内事务方法互调默认失败，原因是绕过了代理对象，事务是通过AOP代理对象增强的方式
 * 解决：使用代理对象来调用事务方法
 * 1）引入aop-starter
 * 2）@EnableAspectJAutoProxy：开启aspectj静态代理功能
 *   exposeProxy = true 对外暴露代理对象
 * 3）本类互相调用通过代理对象
 *
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class DoermailOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoermailOrderApplication.class, args);
    }

}
