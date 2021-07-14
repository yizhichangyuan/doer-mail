package com.lookstarry.doermail.order.service.impl;

import com.lookstarry.doermail.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lookstarry.common.utils.PageUtils;
import com.lookstarry.common.utils.Query;

import com.lookstarry.doermail.order.dao.OrderItemDao;
import com.lookstarry.doermail.order.entity.OrderItemEntity;
import com.lookstarry.doermail.order.service.OrderItemService;

@RabbitListener(queues={"hello-java-queue"}) // 组件必须在容器中才可以监听
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues：声明需要监听的所有队列
     * org.springframework.amqp.core.Message
     * 参数是接收消息的类型
     * 1、可以写成原生消息类型Message，里面有消息头和消息体
     * 2、T<发送消息的类型> 后面添加上获取消息体的类型
     * 3、Channel：当前传输数据的通道
     *
     * Queue：队列可以有很多人都来监听，只要收到消息队列就会删除消息，而且只能有一个人收到此消息
     * 场景：
     *      1）订单服务启动多个：同一个消息，只能有一个客户端能够收到
     *      2）只有一个消息完全处理完，方法运行结束才可以接收到下一个消息
     *
     */
//    @RabbitListener(queues={"hello-java-queue"}) // 组件必须在容器中才可以监听
    @RabbitHandler
    public void receiveMessageTest(Message message,
                                   OrderReturnReasonEntity content,
                                   Channel channel) throws InterruptedException {
        byte[] body = message.getBody(); // 消息体
        MessageProperties properties = message.getMessageProperties(); // 消息头
        System.out.println("接收的消息为" + message + "==>内容：" + content);
        System.out.println("消息处理完成" + content.getName());
        // 通道内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag==>" + deliveryTag);
        try {
            if(deliveryTag % 2 == 0){
                // 手动签收消息，false为非批量签收模式
                channel.basicAck(deliveryTag, false);
                System.out.println("签收了货物..." + deliveryTag);
            }else{
                // 拒绝签收消息
                // 第三个参数requeue为是否重新入队: false直接丢弃 true发回服务器，重新入队
                channel.basicNack(deliveryTag, false, true);
                System.out.println("没有签收了货物..." + deliveryTag);
            }
        } catch (IOException e) {
            // 网络中断会造成异常
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void receiveMessage2(Message message,
                                   String content){
        byte[] body = message.getBody(); // 消息体
        MessageProperties properties = message.getMessageProperties(); // 消息头
        System.out.println("接收的消息为" + message + "==>内容：" + content);
        System.out.println("消息处理完成" + content);
    }
}