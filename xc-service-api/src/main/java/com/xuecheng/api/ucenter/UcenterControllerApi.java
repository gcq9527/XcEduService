package com.xuecheng.api.ucenter;

import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import io.swagger.annotations.Api;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/24 17:45
 */
@Api(value="用户中心",description = "用户中心管理")
public interface UcenterControllerApi {

    public XcUserExt getUserext(String username);


}