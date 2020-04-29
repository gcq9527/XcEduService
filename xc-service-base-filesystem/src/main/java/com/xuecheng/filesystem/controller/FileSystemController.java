package com.xuecheng.filesystem.controller;

import com.xuecheng.api.fileSystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSyStemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/12 14:17
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSyStemService fileSyStemService;

    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile multipartFile, String filetage, String businesskey, String metadata) throws IOException {
        return fileSyStemService.update(multipartFile,filetage,businesskey,metadata );
    }
}