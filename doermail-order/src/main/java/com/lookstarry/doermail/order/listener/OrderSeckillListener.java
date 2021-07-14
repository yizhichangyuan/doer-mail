package com.lookstarry.doermail.order.listener;

import com.lookstarry.common.to.mq.SeckillOrderTo;
import com.lookstarry.doermail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.omg.SendingContext.RunTime;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @PackageName:com.lookstarry.doermail.order.listener
 * @NAME:OrderSeckillListener
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/5 14:36
 */
@RabbitListener(queues = {"order.seckill.order.queue"})
@Component
public class OrderSeckillListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo order, Message message, Channel channel) throws IOException {
        try{
            orderService.createSeckillOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("创建秒杀订单成功...");
        }catch (Exception e){
            // 重新入队
            System.out.println("创建秒杀订单失败：" + e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            throw new RuntimeException(e.getMessage());
        }

    }
}
