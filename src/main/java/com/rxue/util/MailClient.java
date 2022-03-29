package com.rxue.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.text.html.HTML;

/**
 * @author RunXue
 * @create 2022-03-21 09:57
 * @Description
 */
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content){
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //重载方法，第二个参数表示是否支持html解析
            helper.setText(content,true);
            mailSender.send(message);
            //或者
            //mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮箱失败" + e.getMessage());
        }
    }
}
