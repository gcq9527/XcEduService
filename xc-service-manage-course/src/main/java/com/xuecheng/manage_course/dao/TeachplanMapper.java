package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/8 10:24
 */
@Mapper
public interface TeachplanMapper {

    //课程计划查询
    public TeachplanNode selectList(String courseId);
}