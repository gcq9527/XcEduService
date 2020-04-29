package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.manage_cms.config.MongoConfig;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/5 14:50
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {
    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    //存文件
    @Test
    public void tesetGridFs() throws FileNotFoundException {
        //定义file
        File file = new File("C:\\Users\\white\\Desktop\\course.ftl");
        //定义fileInputStream 读取文件 然后存入数据库
        FileInputStream fileInputStream = new FileInputStream(file);
        //插入数据库 参数二名字
        ObjectId objectId = gridFsTemplate.store(fileInputStream,"course.ftl");
        System.out.println(objectId);
    }
    @Test
    //取文件
    public void  queryFiled()throws Exception{
        //根据文件id取出文件
        GridFSFile gridFSFile =
                gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5e8982fb2afd42010cdd7395")));

        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象，获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //从流中读取数据
        String content = IOUtils.toString(gridFsResource.getInputStream(),"utf-8");
        System.out.println(content);
    }
}