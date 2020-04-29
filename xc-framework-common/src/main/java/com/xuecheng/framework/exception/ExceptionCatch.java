package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/3 21:50
 */
@ControllerAdvice //控制器增强  捕获异常
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    //定义map集合 构建
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;

    //定义map的builder对象 去构建
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode>
            builder = ImmutableMap.builder();

    //捕获CustomException异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        LOGGER.error("catch exception:{}",customException.getResultCode());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }

    //捕获Exception异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        LOGGER.error("catch exception:{}",exception.getMessage());

        if (EXCEPTIONS == null){
            EXCEPTIONS = builder.build(); //EXCEPTIONS 构建成功
        }
        //从EXCEPTIONS 中查找异常类型所对应的错误代码
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null){ //说明找到
            return new ResponseResult(resultCode);
        }else{
            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }

    static{
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAN);
    }

}