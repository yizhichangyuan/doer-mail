package com.lookstarry.doermail.order.listener;

import com.lookstarry.doermail.order.entity.OrderEntity;
import com.lookstarry.doermail.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @PackageName:com.lookstarry.doermail.order.listener
 * @NAME:OrderCloseListener
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/28 22:40
 */
@Component
@RabbitListener(queues = {"order.release.order.queue"})
public class OrderCloseListener {
    @Autowired
    OrderService orderService;


    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        try{
            System.out.println("收到过期的订单消息，准备关闭订单" + entity.getOrderSn());
            // 收到消息后手动确认告知收到消息
            orderService.closeOrder(entity);
            // 手动调用支付宝收单，不让用户再支付了
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println("关闭订单发生异常：" + e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }
}
