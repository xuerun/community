package com.rxue.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author RunXue
 * @create 2022-03-26 10:44
 * @Description  业务日志切面
 */

@Component
@Aspect
public class ServiceLogAspect {
    private final static Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.rxue.service.*.*(..))")
    public void pointcut(){}

    //在调用每个业务方法前记录日志
    //用户[ip]在什么时间点访问了什么业务方法
    //JoinPoint封装了SpringAop中切面方法的信息,在切面方法中添加JoinPoint参数,
    // 就可以获取到封装了该方法信息的JoinPoint对象
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
        //获取当前用户的ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null){
            return;
        }
        HttpServletRequest request = attributes.getRequest();//获取请求对象
        String ip = request.getRemoteHost();//获取ip
        //获取时间点
        String now = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());

        //获取方法信息
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

        logger.info(String.format("用户[%s], 在[%s]访问了[%s]", ip, now, target));
    }
}
