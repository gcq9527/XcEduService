package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.http.protocol.RequestContent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/25 16:58
 */
//@Component
public class loginFilterTest extends ZuulFilter {
    //过滤器的类型
    @Override
    public String filterType()
    {
        /**
         * pre： 请求在被路由前执行
         *
         * routing: 在路由请求时调用
         *
         * post: 在routing和error过滤器之后调用
         *
         *
         * error:处理请求发生错误调用
         *
         */
        return "pre";
    }

    //过滤器序号 越小越被优先执行
    @Override
    public int filterOrder() {
        return 0;
    }

    //是否要执行过滤器 true执行过滤器 false不执行过滤器
    @Override
    public boolean shouldFilter() {
        return true;
    }

    //过滤器的内容
    //测试的需求: 过滤所有请求，判断头部是否有Authorization 如果没有则拒绝访问，否则转发到微服务
    @Override
    public Object run() throws ZuulException {

        //上下文对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到reuquest
        HttpServletRequest request = requestContext.getRequest();
        //得到response
        HttpServletResponse response = requestContext.getResponse();

        //得到Authorization头
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)){
            //拒绝访问
            requestContext.setSendZuulResponse(false);
             //设置响应代码
            requestContext.setResponseStatusCode(200);
            //构建响应对象
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
            String jsonString = JSON.toJSONString(responseResult);
            requestContext.setResponseBody(jsonString);
            //转成json，设置contenttype
            response.setContentType("application/json;charset=utf-8");
            return null;
        }

        return null;
    }
}