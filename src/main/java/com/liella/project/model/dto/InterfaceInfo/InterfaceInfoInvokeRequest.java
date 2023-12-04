package com.liella.project.model.dto.InterfaceInfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */

    private Long id;


    private List<Field> requestParams;
    @Data
    public static class Field {
        private String fieldName;
        private String value;
    }

    /**
     * 请求参数
     */
    private String userRequestParams;


    private static final long serialVersionUID = 1L;
}