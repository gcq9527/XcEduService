package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepostiry;
import com.xuecheng.order.dao.XcTaskRepostiry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Date;
import java.util.Optional;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/28 16:47
 */
@Service
public class TaskService {

    @Autowired
    XcTaskRepostiry xcTaskRepostiry;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    XcTaskHisRepostiry xcTaskHisRepostiry;
    //查询前n条任务
    public List<XcTask> findXcTaskList(Date updateTime, int size){
        //设置分页参数
        Pageable pageable = new PageRequest(0,size);
        //查询前n条任务
        Page<XcTask> all = xcTaskRepostiry.findByUpdateTimeBefore(pageable,updateTime);
        List<XcTask> list = all.getContent();
        return list;
    }

    //发布消息
    public void publish(XcTask xcTask,String ex,String routingKey){
        Optional<XcTask> optional = xcTaskRepostiry.findById(xcTask.getId());
        if(optional.isPresent()){
            //向学习服务发送消息
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepostiry.save(one);
        }
    }
    //获取任务
    @Transactional
    public int getTask(String id,int version){
        //通过乐观锁的方式来更新数据库，如果结果大于-说明取到了任务
       return xcTaskRepostiry.updateTaskVersion(id,version);
    }
    //完成任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> optionalXcTask = xcTaskRepostiry.findById(taskId);
        if (optionalXcTask.isPresent()){
            //当前任务
            XcTask xcTask = optionalXcTask.get();
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepostiry.save(xcTaskHis);
            xcTaskRepostiry.delete(xcTask);
        }

    }
}