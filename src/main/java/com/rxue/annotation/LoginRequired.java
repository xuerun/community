package com.rxue.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author RunXue
 * @create 2022-03-23 16:00
 * @Description 是否需要登录  作用在方法上，运行时才有效
 * 起到标识的左右，有这个标记则登录才能访问
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
