package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/9 13:38
 */
public interface SysDictionanyRepository extends MongoRepository<SysDictionary,String> {
    //根据字典分类查询字典信息
    SysDictionary findByDType(String dType);
}