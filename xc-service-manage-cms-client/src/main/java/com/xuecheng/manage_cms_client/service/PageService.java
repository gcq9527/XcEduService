package com.xuecheng.manage_cms_client.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/7 10:44
 */
@Service
public class PageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageService.class);
    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsSiteRepository cmsSiteRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFsBucket;

    /**
     *保存html页面到服务器物理路径
     *      1.通过pageId查询道cmsPage对象信息
     *      2.调用CmsPage对象得getHtmlFileId 拿到htmlFiled信息
     *      3.通过getFileById(htmlFileId) 拿到输入流
     *           1.通过 模板文件id 查询出模板对象
     *                  GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId));
     *           2.通过GridFsFile的id打开下载流   GridFsDownLoadStream gridfsDownLoadStream =  gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
     *           3.通过创建GridFsResource对象  拿到内容
     *                GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
     *                返回gridFSResource.inputStream内容
     * @param pageId
     */
    public void savePageToServerPath(String pageId){
        //根据pageId 查询cmsPage
        CmsPage cmsPage = findCmsPageById(pageId);
        //得到html文件id 从cmspage获取htmlFildId 内容
        String htmlFileId = cmsPage.getHtmlFileId();

        //从gridFs 查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if (inputStream == null){
            LOGGER.error("getFileById InputStream is null,htmlFileld:{}",htmlFileId);
        }
        //得到站点id
       String siteId = cmsPage.getSiteId();
        //根据站点id 查询到站点对象
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //得到站点的物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath(); //站点内配置的大概路径
        //得到页面物理路径  以及page名字
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        //将html文件保存到服务器物理路径上
        FileOutputStream  fileOutputStream = null;
       try {
           //输出流
            fileOutputStream = new FileOutputStream(new File(pagePath));
            //将读取到的内容复制到 要写入的位置
            IOUtils.copy(inputStream,fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
    //根据文件id从GridFs中取出文件
    public InputStream getFileById(String fileId) {
        //文件对象 通过id查询
        GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFsBucket.openDownloadStream(gridFsFile.getObjectId());
        GridFsResource gridFsResource = new GridFsResource(gridFsFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    //根据站点id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if (optional.isPresent()){
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }


    //根据页面id查询页面信息
    public CmsPage findCmsPageById(String pageId){
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }



}