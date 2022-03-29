package com.rxue.entity;


import java.util.HashMap;
import java.util.Map;

/**
 * @author RunXue
 * @create 2022-03-28 10:18
 * @Description
 */
public class Event {
    //事件的主体
    private String topic;
    //谁触发的
    private int userId;
    //触发的实体是谁  userId触发了entityUserId的id为entityId的entityType
    private int entityType;
    private int entityId;
    private int entityUserId;//实体作者

    private Map<String, Object> data = new HashMap<>();//其他数据存入map中

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    //每个set方法都返回Event的话可以进行连续赋值
    //event.setUserid().setEntityId().setData()
    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
