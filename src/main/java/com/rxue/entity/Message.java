package com.rxue.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-25 14:18
 * @Description
 */
@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId;
    private String content;
    private int status;
    private Date createTime;
}
