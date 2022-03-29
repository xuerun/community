package com.rxue.controller;

import com.rxue.annotation.LoginRequired;
import com.rxue.entity.Event;
import com.rxue.entity.Page;
import com.rxue.entity.User;
import com.rxue.event.EventProducer;
import com.rxue.service.FollowService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


/**
 * @author RunXue
 * @create 2022-03-27 12:29
 * @Description 关注和取关的表现层
 */
@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @ResponseBody
    @PostMapping("/follow")
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.sendMessage(event);

        return newCoderUtil.getJsonString(0, "点赞成功");
    }

    @LoginRequired
    @ResponseBody
    @PostMapping("/unfollow")
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return newCoderUtil.getJsonString(0, "已取消点赞");
    }

    //某个用户关注的人的列表
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //分页信息
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map :
                    userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";

    }

    //某个用户的粉丝列表
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //分页信息
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map :
                    userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";

    }

    //是否关注过id为userId的用户
    private boolean hasFollowed(int userId){
        User user = hostHolder.getUser();
        if(user == null){
            return  false;
        }

        return followService.hasFollowed(user.getId(), ENTITY_TYPE_USER, userId);
    }
}
