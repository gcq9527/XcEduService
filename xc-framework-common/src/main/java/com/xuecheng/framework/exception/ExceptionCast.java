package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/3 21:48
 */
public class ExceptionCast {
    //封装异常抛出
    public static void cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}