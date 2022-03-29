package com.rxue.dao;

import com.rxue.entity.Message;

import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-25 14:20
 * @Description
 */
public interface MessageMapper {
    //查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询某个会话所包含的私信列表
    List<Message> selectMessages(String conversationId, int offset, int limit);

    //查询某个会话所包含的私信数量
    int selectMessageCount(String conversationId);

    //查询未读私信的数量  动态sql，如果conversationId为null，则查的是所有的未读私信
    int selectMessageUnreadCount(String conversationId, int userId);

    //新增消息
    int insertMessage(Message message);

    //更改消息的状态（可以同时更改一条或者多条） 如将未读设置成已读 将已读设置成未读
    int updateStatus(List<Integer> ids, int status);

    //查询某个主题下的最新的通知
    Message selectLastNotice(int userId, String topic);

    //查询某个主题下通知的数量
    int selectNoticeCount(int userId, String topic);

    //查询某个主题下未读通知的数量（用动态sql，使其还可以查所有主题的未读通知）
    int selectNoticeUnreadCount(int userId, String topic);

    //查询某个主题所包含的通知列表 支持分页
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
