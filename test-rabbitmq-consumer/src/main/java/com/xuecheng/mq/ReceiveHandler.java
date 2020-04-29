package com.xuecheng.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.manage_cms_client.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/6 16:29
 */
@Component
public class ReceiveHandler {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void sendEamil(String msg, Message message, Channel channel){
        System.out.println("receive message is :" + msg);
    }
}