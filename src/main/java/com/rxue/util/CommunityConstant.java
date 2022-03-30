package com.rxue.util;

/**
 * @author RunXue
 * @create 2022-03-21 16:15
 * @Description 定义一些常量
 */
public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int  ACTIVATION_FAIL = 2;

    /**
     * 默认登录过期时间
     */
    int DEFAULT_EXPIRED_SECONDS = 60 * 12;

    /**
     * 记住我勾选的登录过期时间
     */
    int REMEMBER_EXPIRED_SECONDS = 60 * 60 * 24;

    /**
     * 评论的实体类别（对什么进行评论） 1代表帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 2代表评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 3代表用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题 评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题 点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题 关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题 发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 系统用户id 1
     */
    int SYSTEM_USER_ID = 1;
}
