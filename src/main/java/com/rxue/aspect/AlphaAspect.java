package com.rxue.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author RunXue
 * @create 2022-03-26 09:20
 * @Description 切面组件
 */
@Component
@Aspect//把当前类标识为一个切面
public class AlphaAspect {

    @Pointcut("execution(* com.rxue.service.*.*(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void before(){
        System.out.println("before方法执行");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after方法执行");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning方法执行");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing方法执行");
    }

    /**
     *
     * @param joinPoint 可用于执行切点的类
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around通知前");
        Object obj = joinPoint.proceed();
        System.out.println("around通知后");
        return obj;
    }
}
