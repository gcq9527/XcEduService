package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

     CategoryNode findList();
}
