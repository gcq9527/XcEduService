package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator.
 */
public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    //当返回值大于0 说明删除成功
    long deleteByCourseid(String courseId);
}
