package com.liella.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.liella.liellacommon.model.entity.User;
import com.liella.project.common.BaseResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author liella
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String userName);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 邮箱注册
     * @param userAccount
     * @param code
     * @param userName
     * @return
     */
    long userEmailRegister(String userAccount, String code, String userName);

    /**
     * 更改签名
     * @param id
     * @return
     */
    boolean updateAk(Long id);



    /**
     * 获取验证码
     * @return
     */
    public void sendCode(String username);

    /**
     * 签到
     * @param userID
     * @return
     */
    String sign(Long userID);

    Integer signCount(Long id);
}
