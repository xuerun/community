package com.rxue.config;

import com.rxue.controller.interceptor.AlphaInterceptor;
import com.rxue.controller.interceptor.DataInterceptor;
import com.rxue.controller.interceptor.LoginRequiredInterceptor;
import com.rxue.controller.interceptor.LoginTicketInterceptor;
import com.rxue.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author RunXue
 * @create 2022-03-22 18:13
 * @Description  添加自定义拦截器  自定义拦截器按注册顺序执行
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    //@Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/css/**", "/img/**", "/js/**")
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/css/**", "/img/**", "/js/**");

        //registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/css/**", "/img/**", "/js/**");

        registry.addInterceptor(messageInterceptor).excludePathPatterns("/css/**", "/img/**", "/js/**");

        registry.addInterceptor(dataInterceptor).excludePathPatterns("/css/**", "/img/**", "/js/**");
    }
}
