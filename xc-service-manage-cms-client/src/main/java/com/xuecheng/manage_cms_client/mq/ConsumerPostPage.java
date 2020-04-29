package com.xuecheng.manage_cms_client.mq;

import com.alibaba.fastjson.JSONObject;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听MQ，接收页面发布消息
 * @author yd
 * @version 1.0
 * @date 2020/4/7 11:30
 */
@Component
public class ConsumerPostPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);

    @Autowired
    PageService pageService;

    /**
     * 监听到消息后
     *     1.把内容转换成json格式
     *     2.调用savePageToServerPath(pageId) 保存对象
     * @param msg
     */
    //从配置文件中读取 监听来自cms端的信息
    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String msg){
        //解析消息
        Map map = JSONObject.parseObject(msg);
        //得到消息中的id
        String pageId = (String) map.get("pageId");
        ///验证下对象是否为空
        CmsPage cmsPageById = pageService.findCmsPageById(pageId);
        if (cmsPageById == null){
            LOGGER.error("receive postpage msg,cmspage is null page:{}",pageId);
            return ;
        }
        //调用serivce方法将从页面GridFs中下载页面
        pageService.savePageToServerPath(pageId);
    }
}