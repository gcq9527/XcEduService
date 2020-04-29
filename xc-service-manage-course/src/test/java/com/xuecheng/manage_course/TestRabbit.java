package com.xuecheng.manage_course;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.manage_course.dao.CategoryMapper;
import com.xuecheng.manage_course.dao.CourseBaseRepository;
import com.xuecheng.manage_course.dao.CourseMapper;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRabbit {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CategoryMapper categroyMapper;


    @Test
    public void testRibbon(){
        //确定要获取的服务例表
        String serviceId = "XC-SERVICE-MANAGE-CMS";
        //
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serviceId  + "/cms/page/get/5a754adf6abb500ad05688d9", Map.class);

        Map body = forEntity.getBody();
        System.out.println(body);
    }
}
