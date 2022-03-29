package com.rxue.util;

import com.rxue.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author RunXue
 * @create 2022-03-22 18:48
 * @Description 持有用户信息，用于代替session对象  以线程为key，这样保证了每个线程都是用的同一个user
 * 每台浏览器访问服务器，服务器都会创建一个线程去处理浏览器的请求，这个的HostHolder就是模拟了session的功能
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //请求结束将map清空
    public void clear(){
        users.remove();
    }
}
