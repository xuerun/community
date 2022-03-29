package com.rxue.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.rxue.annotation.LoginRequired;
import com.rxue.entity.Message;
import com.rxue.entity.Page;
import com.rxue.entity.User;
import com.rxue.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-25 16:05
 * @Description
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();
        //分页信息
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        page.setLimit(5);
        
        //会话列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for (Message conversation :
                    conversationList) {
                Map<String, Object> map = new HashMap<>();
                //某个会话，针对每个会话只返回一条最新的私信
                map.put("conversation", conversation);
                //某个会话所包含的私信数量
                map.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));
                //某个会话未读的私信数量
                map.put("unreadLetterCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                //某个会话对应的用户（和谁会话）
                int targetId = conversation.getFromId() == user.getId()?conversation.getToId():conversation.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        //未读的私信的数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        //查询通知的未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    //私信详情
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(Model model, @PathVariable("conversationId") String conversationId, Page page){
        //分页信息
        page.setRows(messageService.findLetterCount(conversationId));
        page.setPath("/letter/detail/" + conversationId);
        page.setLimit(5);

        //私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for (Message message :
                    letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        //私信的目标
        model.addAttribute("target", getLetterTarget(conversationId));

        //将接收到的私信的状态为已读
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readStatus(ids);
        }

        return "/site/letter-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] id = conversationId.split("_");
        int id0 = Integer.parseInt(id[0]);
        int id1 = Integer.parseInt(id[1]);
        User user =  hostHolder.getUser().getId() == id0?
                userService.findUserById(id1):userService.findUserById(id0);
        System.out.println("user = " + user);
        return user;
    }

    //获取接收到的私信是未读状态的id列表集合
    private List<Integer> getLetterIds(List<Message> letterList){
        User user = hostHolder.getUser();
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for (Message message :
                    letterList) {
                //只更改接收的私信的未读状态
                if(message.getStatus() == 0 && message.getFromId() != user.getId()){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @LoginRequired
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendMessage(String toName, String content){
        User user = hostHolder.getUser();
        User target = userService.findByUserName(toName);
        if(target == null){
            return newCoderUtil.getJsonString(1, "目标用户不存在！");
        }
        Message message = new Message();
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(user.getId());
        message.setToId(target.getId());
        //拼接会话id 用户id小的在前面
        String conversationId = user.getId() > target.getId()?
                target.getId() + "_" + user.getId(): user.getId() + "_" + target.getId();
        message.setConversationId(conversationId);
        messageService.addMessage(message);
        return newCoderUtil.getJsonString(0, "私信发送成功！");
    }

    //通知列表
    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();
        //查询评论类的通知
        Message lastNotice = messageService.findLastNotice(user.getId(), TOPIC_COMMENT);
        if(lastNotice != null){
            Map<String, Object> map = new HashMap<>();
            map.put("lastNotice", lastNotice);

            //对内容进行反Html编码转译
            String content = HtmlUtils.htmlUnescape(lastNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            map.put("user", userService.findUserById((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));
            map.put("posdId", data.get("postId"));

            //该主题下全部通知的数量
            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            map.put("noticeCount", noticeCount);
            System.out.println("noticeCount = " + noticeCount);
            //该主题下未读通知的数量
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            map.put("noticeUnreadCount", noticeUnreadCount);
            model.addAttribute("commentNotice", map);
        }


        //查询点赞类的通知
        lastNotice = messageService.findLastNotice(user.getId(), TOPIC_LIKE);

        if(lastNotice != null){
            Map<String, Object> map = new HashMap<>();
            map.put("lastNotice", lastNotice);

            //对内容进行反Html编码转译
            String content = HtmlUtils.htmlUnescape(lastNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            map.put("user", userService.findUserById((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));
            map.put("postId", data.get("postId"));

            //该主题下全部通知的数量
            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            map.put("noticeCount", noticeCount);
            System.out.println("noticeCount = " + noticeCount);
            //该主题下未读通知的数量
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            map.put("noticeUnreadCount", noticeUnreadCount);
            model.addAttribute("likeNotice", map);
        }


        //查询关注类的通知
        lastNotice = messageService.findLastNotice(user.getId(), TOPIC_FOLLOW);

        if(lastNotice != null){
            Map<String, Object> map = new HashMap<>();
            map.put("lastNotice", lastNotice);

            //对内容进行反Html编码转译
            String content = HtmlUtils.htmlUnescape(lastNotice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, Map.class);
            map.put("user", userService.findUserById((Integer) data.get("userId")));
            map.put("entityType", data.get("entityType"));
            map.put("entityId", data.get("entityId"));

            //该主题下全部通知的数量
            int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            map.put("noticeCount", noticeCount);
            System.out.println("noticeCount = " + noticeCount);
            //该主题下未读通知的数量
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            System.out.println("noticeUnreadCount = " + noticeUnreadCount);
            map.put("noticeUnreadCount", noticeUnreadCount);
            model.addAttribute("followNotice", map);
        }


        //查询私信的未读数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        //查询通知的未读数量
        int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page){
        User user = hostHolder.getUser();
        //设置页面信息
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        page.setPath("/notice/detail/" + topic);
        page.setLimit(5);

        //获取一页通知
        List<Message> noticeList = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> notices = new ArrayList<>();
        if(noticeList != null){
            for (Message message :
                    noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", message);
                String content = HtmlUtils.htmlUnescape(message.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, Map.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                //通知作者（就是系统的名字）
                map.put("fromUser", userService.findUserById(message.getFromId()));

                notices.add(map);
                }
            }
        model.addAttribute("notices", notices);
        //设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readStatus(ids);
        }
        return "/site/notice-detail";
    }

}
