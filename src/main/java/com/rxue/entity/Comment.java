package com.rxue.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-24 18:39
 * @Description
 */
@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType;
    private int entityId;
    private int targetId;
    private String content;
    private int status;
    private Date createTime;
}
