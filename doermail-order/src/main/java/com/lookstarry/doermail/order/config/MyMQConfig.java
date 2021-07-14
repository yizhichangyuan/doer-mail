package com.lookstarry.doermail.order.config;

import com.lookstarry.doermail.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @PackageName:com.lookstarry.doermail.order.config
 * @NAME:MyMQConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/26 22:40
 */
@Configuration
public class MyMQConfig {
    /**
     * 延时队列，原理：目的将其中信息熬成死信
     * Spring允许直接@Bean将对应的交换机、队列、Binding放入队列中，就会自动创建
     * 前提是容器中没有同名的要素存在
     * String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        // 队列初始化参数，死亡相关信息
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");   // 死信交换机
        arguments.put("x-dead-letter-routing-key", "order.release.order"); // 死信路由键
        arguments.put("x-message-ttl", 60000L); // 消息存活时间，毫秒为单位

        Queue queue = new Queue("order.delay.queue", true, false, false, arguments);
        return queue;
    }

    @Bean
    public Queue orderReleaseQueue(){
        Queue queue = new Queue("order.release.order.queue", true, false, false, null);
        return queue;
    }

    /**
     * 创建topic交换机，因为需要与上述两个队列绑定，可以利用routing-key模糊匹配的方式
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        TopicExchange topicExchange = new TopicExchange("order-event-exchange", true, false);
        return topicExchange;
    }

    /**
     * 延时队列与交换机的绑定关系
     * String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
     * @return
     */
    @Bean
    public Binding orderCreateOrderBinding(){
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    /**
     * 与消费者连接的队列
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    @Bean
    public Binding orderReleaseOtherBinding(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Queue orderSeckillOrderQueue(){
        return new Queue("order.seckill.order.queue",
                true,
                false,
                false,
                null);
    }

    @Bean
    public Binding orderSeckillOrderBinding(){
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }
}