package com.rxue.util;

/**
 * @author RunXue
 * @create 2022-03-26 17:07
 * @Description 生成redis的key
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String LIKE_ENTITY = "like:entity";
    private static final String LIKE_USER = "like:user";
    private static final String FOLLOWEE = "followee";
    private static final String FOLLOWER = "follower";
    private static final String KAPTCHA = "kaptcha";
    private static final String TICKET = "ticket";
    private static final String USER = "user";

    //生成某个实体的点赞的key
    //格式like：entity：entityType：entityId   值用set存  存的是userId 给点赞的用户的id
    public static String getLikeEntityKey(int entityType, int entityId){
        return LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
    }

    //生成某个用户的获得的点赞的key
    //格式：like：user：userId
    public static String getLikeUserKey(int userId){
        return LIKE_USER + SPLIT + userId;
    }

    //生成某个用户关注的实体key（可能是帖子、可能是用户）  某个用户的关注列表 对应页面的关注了
    public static String getFolloweeKey(int userId, int entityType){
        return FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    //生成某个实体拥有的粉丝key  可能是某个用户被关注了多少 可能是某个帖子被关注了多少
    public static String getFollowerKey(int entityType, int entityId){
        return FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    //生成登录验证码的key owner为随机生成的字符串，作为cookie返回给浏览器
    public static String getKaptchaKey(String owner){
        return KAPTCHA + SPLIT + owner;
    }

    //生成登录凭证的Key
    public static String getTicketKey(String ticket){
        return TICKET + SPLIT + ticket;
    }

    //生成用户id的key
    public static String getUserKey(int userId){
        return USER + SPLIT + userId;
    }
}
