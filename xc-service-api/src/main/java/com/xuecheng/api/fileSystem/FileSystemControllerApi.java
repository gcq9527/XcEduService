package com.xuecheng.api.fileSystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/12 13:21
 */
@Api(value = "文件管理接口",description = "文件管理接口 提供页面的增 删 改 查")
public interface FileSystemControllerApi {

    //上传文件
    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String filetage,
                                   String businesskey,
                                   String metadata) throws IOException;
}