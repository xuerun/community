package com.rxue.controller.interceptor;


import com.rxue.entity.LoginTicket;
import com.rxue.entity.User;
import com.rxue.service.UserService;
import com.rxue.util.CookieUtil;
import com.rxue.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-22 18:29
 * @Description 登录凭证拦截器
 * 在请求开始时查询登录用户
 * 在本次请求中持有用户数据
 * 在模板视图上显示用户数据
 * 在请求结束时清理用户数据
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //在请求之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket != null){
            //查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //判断凭证是否有效  过期时间大于当前时间
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUsers(user);
            }
        }
        return true;
    }

    //在请求之后，在模板引擎之前，所以可以将user加到modelandview中，返回到前端中使用
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);//返回loginUser到前端  如果有这个属性则不显示登录 注册  如果没有这个属性则不显示头像、个人设置等
        }
    }

    //在模板引擎之后，此时前端已经使用过user了，此时将hostHolder清空
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
