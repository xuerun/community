package com.rxue.controller;

import com.rxue.annotation.LoginRequired;
import com.rxue.entity.Comment;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.Event;
import com.rxue.entity.User;
import com.rxue.event.EventProducer;
import com.rxue.service.CommentService;
import com.rxue.service.DiscussPostService;
import com.rxue.util.CommunityConstant;
import com.rxue.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-25 11:46
 * @Description
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    //增加帖子评论
    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event();
        event.setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setEntityUserId(comment.getTargetId())
                .setData("postId", discussPostId);//postId使用来点进帖子查看的


        eventProducer.sendMessage(event);


        return "redirect:/discuss/detail/" + discussPostId;
    }
}
