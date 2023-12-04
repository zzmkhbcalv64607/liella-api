package com.liella.project.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片视图
 * @Date: 2023/08/20 08:21:48
 * @Version: 1.0
 * @Description: 上传图片状态vo
 */
@Data
public class ImageVo implements Serializable {
    private static final long serialVersionUID = -4296258656223039373L;
    private String uid;
    private String name;
    private String status;
    private String url;

}
