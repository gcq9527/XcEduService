package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/9 13:40
 */
@Service
public class SysDictionanyService {

    @Autowired
    SysDictionanyRepository sysDictionanyRepository;

    //根据字典查询
    public SysDictionary findbyDtype(String DType){
        SysDictionary sysDictionary = sysDictionanyRepository.findByDType(DType);
        return sysDictionary;
    }
}