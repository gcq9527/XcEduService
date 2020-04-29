package com.xuecheng.manage_course.exception;

import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.CommonCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 课程管理自定义的异常类
 * @author yd
 * @version 1.0
 * @date 2020/4/26 14:52
 */
@ControllerAdvice //控制器增强
public class CustomExceptionCatch extends ExceptionCatch {

    static{
        builder.put(AccessDeniedException.class, CommonCode.UNAUTHORISE);
    }
}