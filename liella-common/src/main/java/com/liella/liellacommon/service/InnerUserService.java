package com.liella.liellacommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.liella.liellacommon.model.entity.User;


import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author liella
 */
public interface InnerUserService  {
    /**
     * 数据库中查是否分配给该用户密钥（accessKey,secreKey,布尔）
     * @param accessKey
     * @param secretKey
     * @return
     */
    User getInvokeUser(String accessKey, String secretKey);

}
