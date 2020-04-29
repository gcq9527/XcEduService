package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator.
 */
public interface TeachplanRepository extends JpaRepository<Teachplan,String> {

    //查询teachplan 查询SELECT * FROM teachplan a WHERE a.`courseid` = '4028e581617f945f01617f9dabc40000' AND a.`parentid` = '0'a
    public List<Teachplan> findByCourseidAndParentid(String courseId, String parentId);
}
