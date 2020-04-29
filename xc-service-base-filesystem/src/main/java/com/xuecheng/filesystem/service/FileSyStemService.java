package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/12 13:52
 */
@Service
public class FileSyStemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;


    //上传文件
    public UploadFileResult update(MultipartFile multipartFile,
                                   String filetage,
                                   String  businesskey,
                                   String metadata) throws IOException {
        if (multipartFile == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //第一步: 将文件上传到fastDFS中 得到一个文件id
        String fileId = fdfs_update(multipartFile);
        if (StringUtils.isEmpty(fileId)) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //第二步：将文件id及其他文件信息存储到mongodb中
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetage);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if (StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata);
                fileSystem.setMetadata(map);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    /**
     *
     * @param multipartFile 文件
     * @return 文件id
     */
    public String  fdfs_update(MultipartFile multipartFile) throws IOException {
        //初始化fastDFS环境
        initFdfsConfig();
        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //上传文件
            byte[] bytes = multipartFile.getBytes();
            //得到文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到原始文件扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(bytes, ext, null);
            return fileId;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    //初始化fastdfs环境
    private void initFdfsConfig(){
        //初始化tracker服务地址  多个tracker中间使用逗号分隔
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            //报出异常 初始化fastDfs环境出错
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}