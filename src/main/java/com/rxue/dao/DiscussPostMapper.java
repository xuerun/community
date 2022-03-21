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
     * 该方法即可以实现查询某个userid的评论，也可以查询所有id的评论
     * 所以需要用到动态id， 最好把@param加上
     *
     * @param userId 用户的id，
     * @param offset 分页查询的偏移量
     * @param limit  分页查询每页的个数
     * @return 评论列表
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 该方法可以查询某个userid的评论个数，也可以查询所有的评论个数
     * 所以同样需要动态id
     *
     * @param userId 用户的id
     * @return 返回某用户或者所有用户的评论条数
     */
    int selectDiscussPostRows(@Param("userId") int userId);
}
