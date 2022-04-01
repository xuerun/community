package com.rxue.entity;

import com.rxue.service.UserService;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
