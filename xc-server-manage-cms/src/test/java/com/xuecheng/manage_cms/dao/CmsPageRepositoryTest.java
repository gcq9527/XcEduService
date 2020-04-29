package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * Create by on2020/4/1 17:01
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    PageService pageService;
    @Test
    public void testGetPageHtml(){
        String pageHtml = pageService.getPageHtml("5e86e1c52afd4258fcd7a2dc");
        System.out.println(pageHtml);
    }
    /**
     * 测试查询全部
     */
    @Test
    public void testFindAll(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    /**
     * 测试分页查询
     */
    @Test
    public void testFindPage(){
        int page = 1;
        int size = 10;
        //调用提供的方法
        Pageable pageable = PageRequest.of(page,size);
        //pageable根据条件传入
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }

    /**
     *  测试更新
     */
    @Test
    public void testUpdate(){
        //根据id经行更新 先查询 后更新
        Optional<CmsPage> option =cmsPageRepository.findById("5af942190e661827d8e2f5e3");
        if (option.isPresent()){
            CmsPage cmsPage = option.get();
            //设置修改的值
            cmsPage.setPageAliase("test001");
            //修改
           CmsPage  save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }
     }
    //根据页面名称查询
    @Test
    public void testfindByPageName(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("index.html");
        System.out.println(cmsPage) ;
    }
    //自定义条件测试
    /**
     *  使用了ExampleMatcher来定义条件
     */
    @Test
    public void testFindAllType(){
        //分页参数
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page,size);

        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //要查询id为XX的页面
//        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        //页面别名
        cmsPage.setPageAliase("轮播");
//        条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        //设置条件模糊查询 第一个参数是实体的名称
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //ExampleMatcher.GenericPropertyMatchers.contains() 包含关键字
        // ExampleMatcher.GenericPropertyMatchers.statsWith() //前缀匹配
        //定义Example
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);


    }
    @Test
    public void testBySteId(){
        //查询全部数据
        List<CmsPage> list = cmsPageRepository.findAll();
        System.out.println(list);
    }



}






















