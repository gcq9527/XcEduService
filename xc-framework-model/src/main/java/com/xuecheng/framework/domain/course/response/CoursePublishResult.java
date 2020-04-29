package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author yd
 * @version 1.0
 * @date 2020/4/13 22:02
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublishResult extends ResponseResult {
    String previewUrl; //页面预览URL 必须的阿斗页面id才可以瓶装
    public CoursePublishResult(ResultCode resultCode, String previewUrl)
    {
        super(resultCode); this.previewUrl = previewUrl;
    }
}