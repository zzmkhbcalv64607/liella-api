package com.liella.project.service.impl;


import org.thymeleaf.TemplateEngine;
import com.liella.liellacommon.model.dto.MailDTO;
import com.liella.project.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * 邮件服务接口实现类
 *
 * @author  liyuu
 **/
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * 邮箱号
     */
    @Value("${spring.mail.username}")
    private String email;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 发送登录邮件
     * @param mailDTO 邮件信息
     */
    @Override
    public void sendSimpleMail(MailDTO mailDTO) {
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(email);
        simpleMail.setTo(mailDTO.getToEmail());
        simpleMail.setSubject(mailDTO.getSubject());
        simpleMail.setText(mailDTO.getContent());
        javaMailSender.send(simpleMail);
    }

    /**
     * 发送邮件通知成功回复
     * @param mailDTO 邮件信息
     */
    @Override
    public void sendHtmlMail(MailDTO mailDTO) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            Context context = new Context();
            context.setVariables(mailDTO.getContentMap());
            String process = templateEngine.process(mailDTO.getTemplate(), context);
            mimeMessageHelper.setFrom(email);
            mimeMessageHelper.setTo(mailDTO.getToEmail());
            mimeMessageHelper.setSubject(mailDTO.getSubject());
            mimeMessageHelper.setText(process, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}