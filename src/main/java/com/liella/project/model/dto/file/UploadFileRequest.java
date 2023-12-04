package com.liella.project.model.dto.file;

import lombok.Data;

/**
 * 上传文件请求
 */
@Data
public class UploadFileRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 业务
     */
    private String biz;
}
