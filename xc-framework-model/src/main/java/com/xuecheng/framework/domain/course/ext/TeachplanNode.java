package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.Teachplan;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by admin on 2018/2/7.
 */
@Data
@ToString
public class TeachplanNode extends Teachplan {

    List<TeachplanNode> children;

    //媒资文件id
    String mediaId;
    //媒资文件原始名称
    String mediaFileOriginalName;

    public List<TeachplanNode> getChildren() {
        return children;
    }

    public void setChildren(List<TeachplanNode> children) {
        this.children = children;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaFileOriginalName() {
        return mediaFileOriginalName;
    }

    public void setMediaFileOriginalName(String mediaFileOriginalName) {
        this.mediaFileOriginalName = mediaFileOriginalName;
    }
}
