package com.liella.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图
 *
 * @TableName user
 */
@Data
public class UserGetRequest implements Serializable {


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * sk
     */
    private String secretKey;

    /**
     * ak
     */
    private String accessKey;




    private static final long serialVersionUID = 1L;
}