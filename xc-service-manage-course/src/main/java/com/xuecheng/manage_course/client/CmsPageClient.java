package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/13 16:45
 */
@FeignClient(value = "XC-SERVICE-MANAGE-CMS")//指定远程调用的服务名
public interface CmsPageClient {
    //根据页面id查询页面信息 远程调用cms请求数据
    @GetMapping("/cms/page/get/{id}")//用GetMapping 标识
    public CmsPage findCmsPageById(@PathVariable("id") String id);



    //添加页面 用于页面预览
    @PostMapping("/cms/page/save")
    public CmsPageResult saveCmsPage(@RequestBody CmsPage cmsPage);

    //一键发布页面
    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);

}