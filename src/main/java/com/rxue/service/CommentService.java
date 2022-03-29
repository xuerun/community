package com.rxue.service;

import com.rxue.dao.CommentMapper;
import com.rxue.entity.Comment;
import com.rxue.util.CommunityConstant;
import com.rxue.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-24 19:27
 * @Description
 */
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    //查询某页评论
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    //查找评论的数量
    public int findCoutnByEntity(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //添加评论并更新帖子表中的评论数量
    //需要使用事务  要么都成功要么都失败
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //对评论进行html编码转译
        //敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        //添加评论
        int rows = commentMapper.insertComment(comment);
        //更新帖子的评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){//评论的实体类别要是帖子才行
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}
