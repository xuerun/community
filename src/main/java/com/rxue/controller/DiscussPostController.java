package com.rxue.controller;

import com.rxue.entity.Comment;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.Event;
import com.rxue.entity.Page;
import com.rxue.entity.User;
import com.rxue.event.EventProducer;
import com.rxue.service.CommentService;
import com.rxue.service.DiscussPostService;
import com.rxue.service.LikeService;
import com.rxue.service.UserService;
import com.rxue.util.CommunityConstant;
import com.rxue.util.HostHolder;
import com.rxue.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-24 09:31
 * @Description
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return newCoderUtil.getJsonString(403, "你还没有登录哦！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.addDiscussPost(discussPost);

        //触发发帖事件
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.sendMessage(event);

        return newCoderUtil.getJsonString(0, "发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){

        //找到帖子
        DiscussPost discussPost = discussPostService.findDiscussById(discussPostId);
        //找到帖子的作者
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("post", discussPost);
        model.addAttribute("user", user);
        //帖子点赞数和状态
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        //用户没有登录的话也可以看到，要判断用户是否登录 否则报空指针异常
        int likeStatus = hostHolder.getUser() == null? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_POST, discussPostId, hostHolder.getUser().getId());
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("likeStatus", likeStatus);

        
        //评论的分页
        page.setRows(discussPost.getCommentCount());
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        //找到关于帖子的所有评论
        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        //评论Vo的列表（ViewObject）
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for (Comment comment :
                    commentList) {
                //评论Vo（评论和评论的作者、评论回复的数量）
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment", comment);
                //评论的作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //回复的列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复Ov的列表
                List<Map<String, Object>> replyOvList = new ArrayList<>();
                if(replyList != null){
                    for (Comment reply :
                            replyList) {
                        //回复Ov（回复和回复的作者、回复的目标）
                        Map<String, Object> replyOv = new HashMap<>();
                        //回复
                        replyOv.put("reply", reply);
                        //作者
                        replyOv.put("user", userService.findUserById(reply.getUserId()));
                        //回复的目标（对谁进行回复）
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyOv.put("target", target);
                        //回复点赞的数量和状态
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        likeStatus = hostHolder.getUser() == null? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, reply.getId(), user.getId());
                        replyOv.put("likeCount", likeCount);
                        replyOv.put("likeStatus", likeStatus);
                        replyOvList.add(replyOv);
                    }
                }
                commentVo.put("replys", replyOvList);

                //回复的数量
                int replyCount = commentService.findCoutnByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                //评论点赞的数量和状态
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                likeStatus = hostHolder.getUser() == null? 0 : likeService.findEntityLikeStatus(ENTITY_TYPE_COMMENT, comment.getId(), user.getId());
                commentVo.put("likeCount", likeCount);
                commentVo.put("likeStatus", likeStatus);

                commentVoList.add(commentVo);

            }
        }

        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
