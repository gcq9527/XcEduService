package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/13 20:25
 */
@Data
@NoArgsConstructor
@ToString
public class CourseView implements Serializable {
    private CourseBase courseBase; //基础信息
    private CoursePic coursePic; //课程营销
    private CourseMarket courseMarket;//课程图片
    private TeachplanNode teachplanNode;//教学计划

    public CourseBase getCourseBase() {
        return courseBase;
    }

    public void setCourseBase(CourseBase courseBase) {
        this.courseBase = courseBase;
    }

    public CoursePic getCoursePic() {
        return coursePic;
    }

    public void setCoursePic(CoursePic coursePic) {
        this.coursePic = coursePic;
    }

    public CourseMarket getCourseMarket() {
        return courseMarket;
    }

    public void setCourseMarket(CourseMarket courseMarket) {
        this.courseMarket = courseMarket;
    }

    public TeachplanNode getTeachplanNode() {
        return teachplanNode;
    }

    public void setTeachplanNode(TeachplanNode teachplanNode) {
        this.teachplanNode = teachplanNode;
    }
}