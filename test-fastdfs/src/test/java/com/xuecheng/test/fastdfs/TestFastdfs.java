package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/10 19:46
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastdfs {
    @Test
    public void testUpload(){
        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Stroage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建stroageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //向stroage服务器上传文件
            //本地文件的路径
            String filePath = "F:/logo.jpg";
            //上传成功后拿到文件Id
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);
            //group1/M00/00/00/wKifgl6SgiGAVhgoAAOEo7B17Aw305.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载
     */
    @Test
    public void testDownload() throws IOException, MyException {
        //加载fastdfs-client.properties配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        //定义TrackerClient，用于请求TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        //连接tracker
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取Stroage
        StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        //创建stroageClient
        StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
        //下载文件
        String filed = "group1/M00/00/00/wKifgl6SgiGAVhgoAAOEo7B17Aw305.jpg";
        byte[] bytes = storageClient1.download_file1(filed);
        //使用输出流保存文件
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:/logo.jpg"));
        fileOutputStream.write(bytes);
    }


}