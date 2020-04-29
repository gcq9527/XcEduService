package com.xuecheng.auth.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/24 18:06
 */
@FeignClient(value = XcServiceList.XC_SERVICE_UCENTER)
public interface UserClient {
    //根据账号查询信息
    @GetMapping("/ucenter/getuserext")
    public XcUserExt getUserExt(@RequestParam("username")String username);
}