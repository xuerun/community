package com.rxue.entity;

import lombok.Data;

import java.security.Principal;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-22 10:07
 * @Description 登录凭证实体类
 */
@Data
public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
