package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/9 13:42
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysDictionanyTest {

    @Autowired
    SysDictionanyRepository sysDictionanyRepository;

    @Test
    public void testFindByType(){
        SysDictionary findbydtype = sysDictionanyRepository.findByDType("200");
        System.out.println(findbydtype);
    }

}