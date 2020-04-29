package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/9 10:41
 */
@Service
public class CategoryService {
    @Autowired
    CategoryMapper categroyMapper;

    public CategoryNode findList(){
        return categroyMapper.findList();
    }
}