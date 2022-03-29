package com.rxue;

import com.rxue.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author RunXue
 * @create 2022-03-21 10:24
 * @Description
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NewCoderApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired//注入Thymeleaf
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("rxue_ah@163.com", "测试邮箱", "hello,world");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "薛润");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("rxue_ah@163.com", "测试html邮件", content);
    }
}
