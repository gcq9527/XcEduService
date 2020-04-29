package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * @author yd
 * @version 1.0
 * @date 2020/4/28 13:38
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)
    public void receiveFinishChoosecourseTask(XcTask xcTask){
        if (xcTask != null && StringUtils.isNotEmpty(xcTask.getId()) ){
            taskService.finishTask(xcTask.getId());
        }
    }
    @Scheduled(cron="0/3 * * * * *")
    public void sendChoosecoursTask(){
        //得到一分钟前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> xcTaskList = taskService.findXcTaskList(time,1000);
        System.out.println(xcTaskList);
        //调用service发布消息,将添加选课的任务发给mq
        for (XcTask xcTask : xcTaskList){
            //取任务
            if (taskService.getTask(xcTask.getId(),xcTask.getVersion())>0){
                String ex = xcTask.getMqExchange();//要发送的交换机
                String routingKey = xcTask.getMqRoutingkey();//发送消息要带routingkey
                //发送消息
                taskService.publish(xcTask,ex,routingKey);
            }
        }
    }

    //定义任务调式策略
//    @Scheduled(fixedRate = 3000)
//    @Scheduled(cron="0/3 * * * * *")//每隔三秒执行
    public void tesk1(){
        LOGGER.info("======================测试定时任务开始1===============");
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("=====================测试定视任务结束2===============");
    }

    //定义任务调式策略
//    @Scheduled(fixedRate = 3000)
//    @Scheduled(cron="0/3 * * * * *")//每隔三秒执行
    public void tesk2(){
        LOGGER.info("======================测试定时任务开始2===============");
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("=====================测试定视任务结束2===============");
    }
}