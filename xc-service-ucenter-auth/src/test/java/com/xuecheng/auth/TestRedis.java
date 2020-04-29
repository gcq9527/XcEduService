package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/24 9:39
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //创建jwt令牌
    @Test
    public void testRedis(){
        //定义key
        String key =  "user_token:7d2f36d4-99ca-4a06-9ae9-ba73b76eab47";
        //定义value
        Map<String,String> value = new HashMap<>();
        value.put("jwt","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4NzczNTc2OSwianRpIjoiN2QyZjM2ZDQtOTljYS00YTA2LTlhZTktYmE3M2I3NmVhYjQ3IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.bWsW-wn57AAbjJpVR_yJiLwTSQ4fKMXMCOsrXam-ft6jG1IiZMHcWuUsXik2-2ObUBMs-Lec06bpbCB5vOD0hkX-2qBjKHVJmU0eBQxxaKO8x0TxBIIcY5pz_9VtLNqkEN21UOERFqnWrty0mUbGGnzY7TbOlWGskjbR4TQkevAaS7cdebrGz64tL8-WO4IGGlcUEK5UY0VKFoLGjSZ-u77oSVG0lVcoWziCb4buHH_NMExJ0YMu77uXsAAE3Jbd5VCmgky_Z6VTdMNDtPB8JK5PDT3Vppx9mufjkUeZsnkj4sM2rcFrBN6XaNptKhNClkHoj8s_LpF5VONAS-xt1g");
        value.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiI3ZDJmMzZkNC05OWNhLTRhMDYtOWFlOS1iYTczYjc2ZWFiNDciLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4NzczNTc2OSwianRpIjoiNGQ0MTFhZGYtNzhmYS00ODZjLWJhYTgtZTEwN2MzOWQ3MDY2IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.KWnpOtG2k8gCJHDhbBfrtRcO1_C4EFIhpAKdHahJ0zAzJR7udHU17SMTneBhNdvDvYhbQs9BcBeL7aUWXkx1Kk8zdwj_O2hLQ3F1iq6Okt2Y2HYpuL9LJWCguf2VR1eDN3K--Yg54p4-JJTKfpzLW5Aio-g96BrzwdLFl1c9-W1nURI8Uo94Pg1lv9uxpQCuxhtb90sVP2Zufb-hvnkbKwDb3QSQq3yvXWnXu_bmHY_EnyDgzC6XNbXWB_4v4vgyfGiWNfHQW-Ztnnmh73GG-Wlayux5IpJO79w7ZeQHYbFV0xEv4-f5BsaaKQ1jwl9oNRDQlbMD3BfpjSvuBwovsQ");
        String json = JSON.toJSONString(value);
        //存储数据
        stringRedisTemplate.boundValueOps(key).set(json,30,TimeUnit.SECONDS);
        //获取数据
        String string = stringRedisTemplate.opsForValue().get(key);
        System.out.println(string);
        //校验

    }

}