package com.lookstarry.doermall.seckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @PackageName:com.lookstarry.doermail.order.config
 * @NAME:MyRabbitConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/19 15:35
 */
@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
