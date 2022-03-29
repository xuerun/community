package com.rxue.controller;

import com.rxue.util.newCoderUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author RunXue
 * @create 2022-03-21 18:56
 * @Description
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    //cookie示例
    @RequestMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建一个cookie  cookie的键和值作用构造器的参数
        Cookie cookie = new Cookie("code", newCoderUtil.generateUUID());
        //设置cookie的生效范围 什么样的请求才会返回给服务器此cookie
        cookie.setPath("/community/alpha/cookie");
        //设置cookie的生存范围，多长时间后此cookie失效 10分钟
        cookie.setMaxAge(60 * 10);
        //将cookie放入到响应体内， 因为服务器是通过响应头将cookie传递给浏览器的
        response.addCookie(cookie);
        return "set cookie";
    }

    //浏览器再次访问服务器时cookie是携带在请求头中的，
    //该方法的形参也可以是request请求，但是请求头中的cookie是一个数组，还需要遍历才能得到想要的数组
    //@CookieValue注解可以通过键的形式获取到对应cookie的值
    @RequestMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }


    //sessoion 实例
    //Session作为方法的参数 Spring MVC会自动创建，不需要手动生成
    @RequestMapping("session/set")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id", "1");
        session.setAttribute("name", "panhu");
        return "set session";
    }

    @RequestMapping("session/get")
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //ajax实例，因为是异步请求，所以不走视图解析器 直接返回json字符串
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjaxJ(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return newCoderUtil.getJsonString(0, "success!");
    }

}
