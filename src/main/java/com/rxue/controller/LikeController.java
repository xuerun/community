package com.rxue.controller;

import com.rxue.annotation.LoginRequired;
import com.rxue.entity.Event;
import com.rxue.entity.User;
import com.rxue.event.EventProducer;
import com.rxue.service.LikeService;
import com.rxue.util.CommunityConstant;
import com.rxue.util.HostHolder;
import com.rxue.util.newCoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-26 19:12
 * @Description  点赞表现层
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    //使用异步请求  返回json字符串
    @PostMapping("/like")
    @LoginRequired
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();
        System.out.println("entityUserId = " + entityUserId);
        //点赞
        likeService.like(entityType, entityId, user.getId(), entityUserId);
        //点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //点赞的状态
        int likeStatus = likeService.findEntityLikeStatus(entityType, entityId, user.getId());


        //触发点赞事件
        //判断是点赞还是取消点赞，如果是点赞才通知
        if (likeStatus == 1){
            Event event = new Event();
            event.setUserId(user.getId())
                    .setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.sendMessage(event);
        }


        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        return newCoderUtil.getJsonString(0, null, map);
    }
}
