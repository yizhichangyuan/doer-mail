package com.lookstarry.doermail.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @PackageName:com.lookstarry.doermail.order.config
 * @NAME:RabbitMessageConverter
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/6 16:48
 */
@Configuration
public class RabbitMessageConverter {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
