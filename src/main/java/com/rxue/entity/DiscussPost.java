package com.rxue.entity;

import lombok.Data;

import java.security.Principal;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-20 10:13
 * @Description 帖子贴
 */
@Data
public class DiscussPost{
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
