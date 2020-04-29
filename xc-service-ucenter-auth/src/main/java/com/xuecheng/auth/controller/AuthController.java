package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.ext.UserTokenStore;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/24 10:35
 */
@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    String clientId;

    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.clientSecret}")
    String clientSecret;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Autowired
    AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {

        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getPassword())) {
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        //账号
        String username = loginRequest.getUsername();
        //密码
        String password = loginRequest.getPassword();

        //申请令牌
        AuthToken authToken = authService.login(username,password,clientId,clientSecret);

        //用户身份令牌
         String acccess_token = authToken.getAccess_token();
        //将令牌存储到cookie
        this.saveCookie(acccess_token);
        return new LoginResult(CommonCode.SUCCESS,acccess_token);
    }
    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //取出cookie中的身份令牌
        String uid =getTokenCookie();
        //删除redis中的token
        Boolean result = authService.delToken(uid);
        //删除cookie中的token
        this.clearCookie(uid);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    @GetMapping("/userjwt")
    public JwtResult userjwt() {
        //取出cookie中的用户身份令牌
        String uid = this.getTokenCookie();
       if (uid == null){
           return new JwtResult(CommonCode.FAIL,null);
       }
        //用身份令牌从redis中取出jwt令牌
        AuthToken userToken = authService.getUserToken(uid);
       if(userToken != null){
           String jwt_token = userToken.getJwt_token();
           return new JwtResult(CommonCode.SUCCESS,jwt_token);
       }
        //将jwt令牌返回给用户
        return null;
    }


    //将令牌保存到cookie
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);
    }


    //取出cookie中的身份令牌
    private String getTokenCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, String> map = CookieUtil.readCookie( request, "uid");
        if(map != null && map.get("uid")!=null){
            String uid = map.get("uid");
            return uid;
        }
        return null;
    }


    //从cookie中删除token
    private void clearCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        //删除cookie就是有效期改成0
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,0,false);

    }


}