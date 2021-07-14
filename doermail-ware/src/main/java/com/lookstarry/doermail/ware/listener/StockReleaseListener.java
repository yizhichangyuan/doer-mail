package com.lookstarry.doermail.ware.listener;

import com.lookstarry.common.to.mq.OrderTo;
import com.lookstarry.common.to.mq.StockLockedTo;
import com.lookstarry.doermail.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @PackageName:com.lookstarry.doermail.ware.listener
 * @NAME:StockReleaseListener
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/6/28 15:06
 */
@RabbitListener(queues={"stock.release.stock.queue"})
@Component
public class StockReleaseListener {
    @Autowired
    WareSkuService wareSkuService;

    /**
     * 库存解锁场景：
     * 1）下订单成功，但订单过期没有支付被系统自动取消或用户手动取消，都要解锁库存
     * 2）下订单成功，库存锁定成功，但是接下来例如扣减用户积分业务调用失败，导致订单回滚，之前锁定成功的库存也要进行解锁
     * 只要解锁库存的消息失败，一定要告诉服务解锁失败，所以需要手动ack
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        try{
            // 当前消息是否为第二次及以后重新派发过来的，并根据DeliveryTag查询防重表中数据库该消息处理状态
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            wareSkuService.unlockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println("解锁库存发生异常：" + e.getMessage());
            // true为消息是否重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
        try{
            System.out.println("订单关闭准备解锁库存...");
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            System.out.println("解锁库存发生异常：" + e.getMessage());
            // true为消息是否重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
