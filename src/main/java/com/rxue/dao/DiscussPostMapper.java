package com.rxue.dao;

import com.rxue.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-20 10:55
 * @Description
 */
public interface DiscussPostMapper {

    /**
     * 该方法即可以实现查询某个userid的帖子，也可以查询所有id的帖子
     * 所以需要用到动态id， 最好把@param加上
     *
     * @param userId 用户的id，
     * @param offset 分页查询的偏移量
     * @param limit  分页查询每页的个数
     * @param orderMode mode等于1排序方式中要加上分数 0表示按最新查询 1表示按最热查询
     * @return 评论列表
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit, int orderMode);

    /**
     * 该方法可以查询某个userid的评论个数，也可以查询所有的评论个数
     * 所以同样需要动态id
     *
     * @param userId 用户的id
     * @return 返回某用户或者所有用户的评论条数
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateScore(int id, double score);
}
