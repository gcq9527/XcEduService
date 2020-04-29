package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.awt.print.Pageable;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq入门程序
 * @author yd
 * @version 1.0
 * @date 2020/4/6 13:56
 */
public class Producer01 {

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
        Connection connection = null;
        Channel channel = null;
        try{
            //建立新连接
            connection = connectionFactory.newConnection();
            //创建会话通道
            channel = connection.createChannel();
            //声明队列
            //参数名称
            /**
             * 1.queue 队列名称
             * 2.durable 是否持久化 如估持久化 mq重启后队列还在
             * 3.exclusive 是否独占连接 队列只应许在连接中访问 如果connection连接关闭队列则自动删除 如果将此参数设置为true 可以用临时队列的创建'
             * 4.autoDelete 自动删除 队列不在使用是否自动删除队列 如果将此参数和 exclusive 参数设置为true 就可以实现临时队列 队列不用自动删除
             * 4.arguments 参数 可以设置一个队列的扩展参数 比如 可设置存活时间
             * */
            channel.queueDeclare(QUEUE,true,false,false,null);
            //发送消息
            /**
             * 参数明细
             * 1.exchange 交换机 如果不指定将使用mq的默认交换机 设置为 ""
             * 2.routingkey 路由key 交换机根据路由key来将消息转发到指定的队列 如果使用默认的交换机 routingkey设置
             * 3.props 消息的属性
             * 4.body 消息内容
             */
            String message = "hello world 黑马程序员";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to rabbitmq");
        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            //关闭连接
            //关闭通道
            channel.close();
            connection.close();
        }
        //和mq建立连接

    }
}