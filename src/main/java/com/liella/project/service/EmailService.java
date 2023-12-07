package com.liella.project.service;


import com.liella.liellacommon.model.dto.MailDTO;

/**
 * 邮件服务接口
 *
 * @author  liyuu
 **/
public interface EmailService {

    /**
     * 发送简单邮件
     *
     * @param mailDTO 邮件信息
     */
    void sendSimpleMail(MailDTO mailDTO);

    /**
     * 发送HTML邮件
     *
     * @param mailDTO 邮件信息
     */
    void sendHtmlMail(MailDTO mailDTO);
}