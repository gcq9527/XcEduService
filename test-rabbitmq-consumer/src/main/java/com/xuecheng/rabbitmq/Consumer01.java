package com.xuecheng.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/6 14:15
 */
public class Consumer01 {

    //队列
    private static final String QUEUE ="hello_world";


    public static void main(String[] args) throws IOException, TimeoutException {
        //通过连接工厂2 创建新的连接和mq建立连接'
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机 一个mq的服务可以设置多个虚拟机 没个虚拟机就相当于一个独立的mq

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE,true,false,false,null);

        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            /**
             *   当接收到消息后此方法会被调用
             * @param consumerTag  消费者标签  用来标识消费者 在监听队列设置 channel.basicConsumme
             * @param envelope 信封 通过envelope
             * @param properties
             * @param body
             * @throws IOException
             */

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息id mq在channel 中用来标识消息的id 可用于确认消息已接收
                long deliverTag = envelope.getDeliveryTag();
                //消息内存
                String message = new String(body,"utf-8");
                System.out.println("receive message: "+ message);

            }
        };
        //监听队列
        /**
         * 参数1 queue 队列名臣
         * 参数2 autoAck 自动回复 true自动回复
         * 参数3 callback 消费方法 当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE,true,defaultConsumer);
    }
}