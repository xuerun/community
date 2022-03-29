package com.rxue.dao;

import com.rxue.entity.Comment;

import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-24 18:53
 * @Description
 */
public interface CommentMapper {
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
