package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.ext.UserTokenStore;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/24 10:37
 */
@Service
public class AuthService {

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //用户认证 申请令牌
    public AuthToken login(String username, String password, String clientId, String clientSecret) {

        //请求spring security申请令牌 申请令牌就会验证用户登录名是否有效
        AuthToken authToken = applyToken(username, password, clientId, clientSecret);
        if (authToken == null){
            //获取令牌出错
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FALL);
        }
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //存储到redis中的内容
        String jsonString = JSON.toJSONString(authToken);
        //将令牌存储到redis中
        Boolean aBoolean = this.saveToken(access_token, jsonString, tokenValiditySeconds);
        if (!aBoolean){
            //存储令牌出错
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFALL);
        }
        return authToken;
    }
    //申请令牌方法
    private AuthToken  applyToken(String username,String password,String clientId,String clientSecret){
        //从eureka中获取认证服务的地址 (因为Spring security在认证服务中)
        //从eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http:ip:port
        URI uri = serviceInstance.getUri();
        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";
        //定义header
        LinkedMultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization",httpBasic);

        //定义body
        LinkedMultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);


        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body,header);
        //设置restTemplate远程调用时候，对400和401不让报错，正确返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //远程调用 参数一 请求地址 参数二 请求方式post 参数三 请求内容设置了 header以及body 参数4返回类型
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);
        //申请令牌信息
        Map bodyMap = exchange.getBody();
        if(bodyMap == null ||
                bodyMap.get("access_token") == null ||
                bodyMap.get("refresh_token") == null ||
                bodyMap.get("jti") == null){
            //解析spring securit{y
            if (bodyMap != null && bodyMap.get("error_description")!=null){
                String error_description = (String) bodyMap.get("error_description");
                if (error_description.indexOf("UserDetailsService returned null") >=0){
                    //账户不存在
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }else if (error_description.indexOf("坏的凭证")>=0){
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }

            return null;
        }
        AuthToken authToken = new AuthToken();
        authToken.setJwt_token((String) bodyMap.get("access_token"));//jwt令牌
        authToken.setRefresh_token((String) bodyMap.get("refresh_token"));//刷新令牌
        authToken.setAccess_token((String) bodyMap.get("jti"));//用户身份认证令牌
        return authToken;
    }


    //获取httpbasic的串
    private String getHttpBasic(String clientId,String clientService){
        String string = clientId + ":" + clientService;
        //将串进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic " + new String(encode);
    }

    //存储到redis中

    /**
     *
     * @param access_token 用户身份令牌
     * @param content 内容就是AuthToken对象的内容
     * @param ttl 过期时间
     * @return
     */
    private Boolean saveToken(String access_token,String content,long ttl){
        String key = "user_token:" + access_token;
        stringRedisTemplate.boundValueOps(key).set(content,ttl, TimeUnit.SECONDS);//过期时间秒
        //取内容 大于0就是成功
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }

    //从reids查询令牌
    public  AuthToken getUserToken(String token){
        String key = "user_token:" + token;
        //从redis中取到的令牌信息
        String value = stringRedisTemplate.opsForValue().get(key);
        //转成对象
        try{
            //将json转换成对象
            AuthToken authToken = JSON.parseObject(value,AuthToken.class);
            return authToken;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    //删除token
    public Boolean delToken(String access_token){
        String key = "user_token:" + access_token;
        stringRedisTemplate.delete(key);
        //取内容 小于0就是成功
//        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return true;
    }
}