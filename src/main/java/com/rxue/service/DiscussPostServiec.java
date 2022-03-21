package com.rxue.service;

import com.rxue.dao.DiscussPostMapper;
import com.rxue.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RunXue
 * @create 2022-03-20 13:04
 * @Description
 */
@Service
public class DiscussPostServiec {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPost(int usreId, int offset, int limit){
        return  discussPostMapper.selectDiscussPosts(usreId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
