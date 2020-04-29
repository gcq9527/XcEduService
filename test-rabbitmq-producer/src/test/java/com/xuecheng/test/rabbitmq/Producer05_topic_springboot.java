package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/6 16:20
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer05_topic_springboot {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     */
    @Test
    public void testSendEamil(){
        String message = "send email message to user";
        /**
         * 参数:
         * 1.交换机名称
         * 2.routingKey
         * 3.消息内容
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);

    }

}