package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/21 9:04
 */
@Api(value = "，媒体文件管理",description = "媒体文件管理接口",tags = {"媒体"})
public interface MediaFileControllerApi {
    @ApiOperation("我的媒资文件查询列表")
    public QueryResponseResult<MediaFile> findList(int age, int size, QueryMediaFileRequest queryMediaFileRequest);

    @ApiOperation("开始处理某个文件")
    public ResponseResult process(String id);

    @ApiOperation("删除媒资文件")
    public ResponseResult deleteMedia(String id);

}