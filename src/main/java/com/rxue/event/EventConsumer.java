package com.rxue.event;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.mysql.cj.log.Log;
import com.rxue.entity.DiscussPost;
import com.rxue.entity.Event;
import com.rxue.entity.Message;
import com.rxue.service.DiscussPostService;
import com.rxue.service.ElasticsearchService;
import com.rxue.service.MessageService;
import com.rxue.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * @author RunXue
 * @create 2022-03-28 10:29
 * @Description 消费者事件
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private DiscussPostService discussPostService;

    @Value("${wk.image.command}")
    private String wkCommand;

    @Value("${wk.image.storage}")
    private String image_storage;


    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息不能为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误");
            return;
        }

        //发送站内消息
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //把其他的属性都放进message表的content中
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if(event.getData() != null){
            for (Map.Entry<String, Object> entry:
                 event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishDiscuss(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息不能为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误");
            return;
        }
        DiscussPost discussPost = discussPostService.findDiscussById(event.getEntityId());
        elasticsearchService.saveDiscussPost(discussPost);
    }

    //消费删除事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteDiscuss(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息不能为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    //消费分享事件
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShare(ConsumerRecord record){
        if(record == null || record.value() == null){
            logger.error("消息不能为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            logger.error("消息格式错误");
            return;
        }

        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = wkCommand + " --quality 75 " + htmlUrl + " " + image_storage + "/" + fileName + suffix;
        //生成长图
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功：" + cmd);
        } catch (IOException e) {
            logger.error("生成长图失败：" + e.getMessage());
        }
    }
}

