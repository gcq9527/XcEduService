package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CategoryMapper categroyMapper;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }
    }
    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);
    }
    @Test
    public void testfindTeachplan(){
        TeachplanNode teachplanNode = teachplanMapper.selectList("4028e581617f945f01617f9dabc40000");
        System.out.println(teachplanNode);
    }

    @Test
    public void testFindCourseInfo(){
        PageHelper.startPage(0,5);
        Page<CourseInfo> couseInfo = courseMapper.findCourseListPage(new CourseListRequest());
        System.out.println(couseInfo);
    }
    @Test
    public void testFindCategoryList(){
        CategoryNode list = categroyMapper.findList();
        System.out.println(list);
    }
    @Test
    public void testGetCourseBaseById(){
        Optional<CourseBase> byId = courseBaseRepository.findById("40281f81715df35f01715df4f8fb0000");
        if (byId.isPresent()){
            System.out.println(byId);
        }
    }
}
