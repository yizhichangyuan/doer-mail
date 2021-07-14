package com.lookstarry.doermail.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @PackageName:com.lookstarry.doermail.ware.config
 * @NAME:MyRabbitConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/27 14:32
 */
@Configuration
public class MyRabbitConfig {
    /**
     * 使用JSON序列化机制，进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * (String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange", true, false, null);
    }

    /**
     * 延时队列，定时50分钟成为死信
     * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 2 * 60000L);
        Queue queue = new Queue("stock.delay.queue", true, false, false, arguments);
        return queue;
    }

    /**
     * 解锁库存队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue(){
        Queue queue = new Queue("stock.release.stock.queue", true, false, false, null);
        return queue;
    }

    /**
     * 死信队列与交换机绑定关系
     * String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Binding stockLockedBinding(){
        return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock-event-exchange", "stock.locked", null);
    }

    /**
     * 解锁库存队列与交换机绑定关系，路由键为模糊路由键
     * @return
     */
    @Bean
    public Binding stockReleaseBinding(){
        return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE, "stock-event-exchange", "stock.release.#", null);
    }
}
