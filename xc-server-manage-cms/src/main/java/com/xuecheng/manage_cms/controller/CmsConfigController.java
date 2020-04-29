package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsConfigControllerApi;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/5 10:29
 */
@RestController
@RequestMapping("/cms")
public class CmsConfigController implements CmsConfigControllerApi {
    @Autowired
    PageService pageService;

    @Override
    @GetMapping("getmodel/{id}")
    public CmsConfig getmodel(@PathVariable  String id) {
        return pageService.getConfigById(id);
    }
}