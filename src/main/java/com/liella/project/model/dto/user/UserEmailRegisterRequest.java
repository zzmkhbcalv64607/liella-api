package com.liella.project.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author liella
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;
    //昵称
    private String userName;
    //邮箱
    private String emailAccount;
    //验证码
    private String captcha;
    //邀请码
    private String invitationCode;


}
