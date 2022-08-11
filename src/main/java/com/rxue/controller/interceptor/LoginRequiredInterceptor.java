package com.rxue.controller.interceptor;

import com.rxue.annotation.LoginRequired;
import com.rxue.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author RunXue
 * @create 2022-03-23 16:07
 * @Description 拦截所有请求，只处理带有该注解的方法
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;
    //在请求前进行拦截 handle是被拦截的目标
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断拦截目标是否是一个方法
        //如果拦截的是一个方法的话，handler就是HandlerMethod这个类型
        if(handler instanceof HandlerMethod){
            //转型为HandlerMethod
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            //读取注解  如果该注解存在，且hostHolder中没有user（说明没有登录），则重定向到登录页面，返回false，不放行，
            LoginRequired annotation = method.getAnnotation(LoginRequired.class);
            if(annotation != null && hostHolder.getUser() == null){
                response.sendRedirect(request.getContextPath() + "/login");//request.getContextPath() = /community
                return false;
            }
        }
        return true;
    }
}
