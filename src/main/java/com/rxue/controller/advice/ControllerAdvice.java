package com.rxue.controller.advice;


import com.rxue.util.newCoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author RunXue
 * @create 2022-03-26 08:20
 * @Description 同一处理异常
 */
//annotations参数表明只扫描带有@Controller注解的bean
@org.springframework.web.bind.annotation.ControllerAdvice(annotations = Controller.class)
public class ControllerAdvice {

    private final static Logger logger = LoggerFactory.getLogger(ControllerAdvice.class);

    //处理的异常类型
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //使用日志记录异常
        logger.error("服务器发生异常！" + e.getMessage());
        //遍历栈的信息
        for (StackTraceElement element:
             e.getStackTrace()) {
            logger.error(element.toString());
        }

        //判断是异步请求还是普通请求
        //异步请求返回json 普通请求重定向到error
        //从请求的消息头中获取值是否是XMLHttpRequest 是的话就是异步请求
        if("XMLHttpRequest".equals(request.getHeader("x-requested-with"))){
            //返回普通字符串
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(newCoderUtil.getJsonString(1, "服务器发生异常！"));
        }else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
