package com.lookstarry.doermail.order;

import com.lookstarry.doermail.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DoermailOrderApplicationTests {

//    @Test
//    void contextLoads() {
//    }
    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 使用AmqpAdmin创建Exchange
     */
    @Test
    public void testCreateExchange(){
        /**
         * public DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false,  null);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange:{}创建成功", directExchange.getName());
    }

    /**
     * 使用AmqpAdmin创建Queue
     */
    @Test
    public void testCreateQueue(){
        /**
         * 队列名称、是否持久化、是否排他（只能独占一条连接）、是否自动删除、参数
         * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         */
        Queue queue = new Queue("hello-java-queue", true, false, false, null);
        amqpAdmin.declareQueue(queue);
        log.info("queue:{}创建成功", queue.getName());
    }

    /**
     * 使用AmqpAdmin创建Binding，连接Exchange和Queue
     */
    @Test
    public void testCreateBinding(){
        /**
         * 目的地、目的地类型、交换机名称、路由键、参数
         * 将exchange指定的交换机和destination目的地进行绑定，使用routingKey作为指定的路由键
         * public Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments)
         */
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding创建成功");
    }

    /**
     * 测试发送消息
     */
    @Test
    public void sendMessageTest(){
        /**
         * 交换机名称、路由键、传递对象会自动序列化
         * public void convertAndSend(String exchange, String routingKey, Object object)
         */
//        String msg ="hello world";
//        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", msg);
//        log.info("消息{}已发送完成", msg);

        /**
         * 复杂对象作为消息发送，会使用序列化机制将对象写出去，要求对象必须实现Serializable
         * 可以在容器中注入MessageConverter将对象序列化为JSON
         */
        for(int i = 0; i < 10; i++){
            if(i % 2 == 0){
                OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setName("test" + i);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setStatus(1);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString()));
            }else{
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello22.java", "hello world" + i, new CorrelationData(UUID.randomUUID().toString()));
            }
        }
    }


}
