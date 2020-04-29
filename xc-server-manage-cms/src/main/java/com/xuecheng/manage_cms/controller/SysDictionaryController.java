package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDictionanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/9 13:06
 */
@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysDictionanyService sysDictionanyService;

    @Override
    @GetMapping("/get/{type}")
    public SysDictionary getByType(@PathVariable String type) {
        return sysDictionanyService.findbyDtype(type);
    }
}