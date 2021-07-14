package com.lookstarry.doermail.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @PackageName:com.lookstarry.doermail.order.config
 * @NAME:RabbitConfig
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/7/6 16:47
 */
@Configuration
public class RabbitConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    //    /**
//     * 定制RabbitTemplate
//     */
    @PostConstruct // MyRabbitConfig调用构造器创建完成以后最后执行该方法
    public void initRabbitTemplate(){
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息正确抵达了Broker，就会执行该逻辑，ack=true
             * @param correlationData 当前消息的唯一关联数据（这个是消息的唯一id）
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                /**
                 * 做好消息确认机制（publisher, consumer）
                 */
                // 服务器收到了，就标志持久化就成功了
                System.out.println("confirm...correlationData[" +correlationData + "]==>ack[" + ack + "]==>cause[" + cause + "]");
            }
        });

        // 消息未成功抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就会出发这个失败回调
             * @param message 投递失败的消息详细信息
             * @param replayCode 回复的状态码
             * @param replayText 回复的文本内容
             * @param exchange 当时这个消息发送给哪个交换机
             * @param routingKey 该消息的消息头使用的路由键
             */
            @Override
            public void returnedMessage(Message message, int replayCode, String replayText, String exchange, String routingKey) {
                // 消息未达到指定队列，修改数据库当前消息的状态，错误
                System.out.println("Fail Message[" + message + "]==>relayCode[" + replayCode + "]==>replayText[" + replayText + "]==>routingKey[" + routingKey + "]");
            }
        });
    }
}
