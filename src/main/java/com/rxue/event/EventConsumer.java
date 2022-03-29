package com.rxue.event;

import com.alibaba.fastjson.JSONObject;
import com.rxue.entity.Event;
import com.rxue.entity.Message;
import com.rxue.service.MessageService;
import com.rxue.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.swing.border.EmptyBorder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-28 10:29
 * @Description 消费者事件
 */
@Component
public class EventConsumer implements CommunityConstant {
    @Autowired
    private MessageService messageService;

    private final static Logger loger = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record){
        if(record == null || record.value() == null){
            loger.error("消息不能为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null){
            loger.error("消息格式错误");
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
}
