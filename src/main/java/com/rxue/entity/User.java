package com.rxue.entity;

import lombok.Data;

import java.security.Principal;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-19 21:35
 * @Description 用户信息
 */
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;//盐 在加密密码后面加一段随机的字符串，更加安全
    private String email;
    private int type;
    private int status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
