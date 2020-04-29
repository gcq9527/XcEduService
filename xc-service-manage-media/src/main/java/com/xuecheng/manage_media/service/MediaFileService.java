package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.support.ExampleMatcherAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/19 15:51
 */
@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;


    //查询媒资文件
    public QueryResponseResult<MediaFile> findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //条件对象
        MediaFile mediaFile = new MediaFile();
        //不为空赋值
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }

        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                                .withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains())//模糊查询
                                .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains());
//                                .withMatcher("processStatus",ExampleMatcher.GenericPropertyMatchers.exact()); 如果不设置匹配 默认是精确匹配
        
        //分页查询对象
        if (page <= 0){
            page = 1;
        }
        page = page - 1;
        if (size<=0){
            size = 10;
        }
        //分页
        Pageable pageable = new PageRequest(page,size);
        //定义example条件对象
        Example<MediaFile> example = Example.of(mediaFile,exampleMatcher);
        //查询全部设置分页
        Page<MediaFile> all = mediaFileRepository.findAll(example, pageable);
        //总记录数
        long totalElements = all.getTotalElements();
        //数据列表
        List<MediaFile> content = all.getContent();
        //返回数据集
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setList(content);//列表
        queryResult.setTotal(totalElements);//总记录数
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //根据id删除媒资文件
    public ResponseResult deleteMedia(String id) {
        //查询是否有改对象
        //搜索为空
        Optional<MediaFile> mediaOptional = mediaFileRepository.findById(id);
        if (!mediaOptional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAN);
        }
        //删除
        MediaFile mediaFile =  mediaOptional.get();
        mediaFileRepository.delete(mediaFile);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}