package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import javafx.util.BuilderFactory;
import jdk.management.resource.ResourceRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.*;
import java.util.*;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/19 15:51
 */
@Service
public class MediaUploadService {
    @Autowired
    MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    String routingkey_media_video;

    @Autowired
    RabbitTemplate rabbitTemplate;

    //注入指定文件位置
    @Value("${xc-service-manage-media.upload-location}")
    String upload_location;
    //得到总父目录文件地址
    private String getFileFolderPath(String fileMd5){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
    }
    //得到视频文件位置
    private String getFilePath(String fileMd5,String fileExt){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }
    //得到块文件地址
    private String getChunkFileFolderPath(String fileMd5){
        return upload_location + fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/chunk/";
    }
    /***
     * 业务流程
     *      1.
     *
     * 根据文件md5得到文件路径
     * * 规则：
     * * 一级目录：md5的第一个字符
     * * 二级目录：md5的第二个字符
     * * 三级目录：md5 * 文件名：md5+文件扩展名 *
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
    */
    //文件上传前的注册 检查文件是否存在
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //检查文件再磁盘上是否存在
        //文件所属目录的路径
        String fileFolderPath = getFileFolderPath(fileMd5);
         //文件的路径
        String filePath = this.getFilePath(fileMd5,fileExt);
        File file = new File(filePath);
        //文件是否存在
        boolean exists = file.exists();
        //2.检查文件信息再mongodb中是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        //文件存在，并且再mongodb中查询到数据
        if(exists && optional.isPresent()){
            //返回错误文件存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        //文件不存在时准备一些准备工作，检查文件所在目录是否存在，如果不存再则去创建
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()){
            //不存在创建文件夹 不单单是一个
            fileFolder.mkdirs();
        }
        //返回执行成功
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 分块检查
     * @param fileMd5
     * @param chunk 块的下标
     * @param chunkSize 块的大小
     */
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        //检查分块文件是否存在
        //得到分块文件的所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //块文件
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()){
            //块文件存在
            return new CheckChunkResult(CommonCode.SUCCESS,true);
        }else{
            //块文件不存在
            return new CheckChunkResult(CommonCode.SUCCESS,false);
        }
    }

    /**
     *  上传分块
     * @param file
     * @param chunk
     * @param fileMd5
     * @return
     */
        public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String  fileMd5) {
        //检查分块目录，如果不存在则要自动创建
        //得到分块目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //得到分块文件路径
        String chunkFilPath = chunkFileFolderPath + chunk;

        File chunkFileFolder = new File(chunkFileFolderPath);
        //如果不存再则要自动创建
        if (!chunkFileFolder.exists()) {
            chunkFileFolder.mkdir();
        }
        //得到上传文件的输入流
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try{
            inputStream = file.getInputStream();
            fileOutputStream = new FileOutputStream(new File(chunkFilPath));
            IOUtils.copy(inputStream,fileOutputStream);
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 合并文件
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1.合并所有分块
        //得到分块文件的目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFilFolder = new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunkFilFolder.listFiles();
        List<File> fileList = Arrays.asList(files);
        //创建一个合并文件
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);
        //执行合并
        mergeFile = this.mergeFile(fileList, mergeFile);
        if (mergeFile == null){
            //合并文件出错
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        
        //2校验文件的md5的值是否和前端传入的md5一致
        boolean checkFileMd5 = this.checkFileMd5(mergeFile, fileMd5);
        if (!checkFileMd5){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //3. 将文件的信息写入到mongodb
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileName(fileMd5 + "." +fileExt);
        //文件路径保存相对路径
        String filePath1 = fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(filePath1);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //文件上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理信息  将avi文件转换成mp4 以及 m3u8
        this.sendProcessVideoMsg(mediaFile.getFileId());

        return new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 发送视频处理消息
     * @param mediaId  文件id
     * @return
     */
    public ResponseResult sendProcessVideoMsg(String mediaId){

        //查询数据库mediaFile
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //构建消息内容
        Map<String,String> map = new HashMap<>();
        map.put("mediaId",mediaId);
        String jsonString = JSON.toJSONString(map);
        //向MQ发送视频处理信息
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK, routingkey_media_video, jsonString);
        }catch (Exception e){
            e.printStackTrace();
            //操作失败返回错误
            return new ResponseResult(CommonCode.FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);

    }

    //校验文件
    private boolean checkFileMd5(File mergeFile,String md5){
        //创建文件输入流
        try {
            FileInputStream fileInputStream = new FileInputStream(mergeFile);
            //得到文件的md5
            String md5Hex = DigestUtils.md5Hex(fileInputStream);

            //和传入的md5比较 相同返回true 出现异常返回false
            if (md5.equalsIgnoreCase(md5Hex)){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;

    }
    //参数1 块列表 参数2 块文件的路径
    private File mergeFile(List<File> chunkFileList, File mergeFile){
        try {
            //块文件存在进行删除
            if (mergeFile.exists()) {
                mergeFile.delete();
            } else {
                //创建一个新文件
                mergeFile.createNewFile();
            }
            //对块文件进行排序
            Collections.sort(chunkFileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())){
                        return 1;
                    }
                    return -1;
                }
            });
                    //创建一个写对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            byte[] b = new byte[1024];
                    for (File chunkFile:chunkFileList){
                        RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                        int len = -1;
                        while((len = raf_read.read(b)) != -1){
                            raf_write.write(b,0,len);
                        }
                        raf_read.close();
                    }
                    raf_write.close();
                    return mergeFile;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}