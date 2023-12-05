package com.liella.project.model.dto.InterfaceInfo;

import lombok.Data;

/**
 * 提交接口调用申请请求
 */
@Data
public class ApplyRequest {
    /**
     * 接口id
     */
    private Long interfaceId;


    /**
     * 用户id
     */
    private Long userId;

    //todo 增加申请方式
}
