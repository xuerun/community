package com.rxue.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author RunXue
 * @create 2022-03-22 18:31
 * @Description  从request中获取指定key的cookie
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }

        //从request中获取cookie数组
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie :
                    cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
