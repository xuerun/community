package com.rxue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan("com.rxue.dao")
public class NewCoderApplication {

    //@PostConstruct
    //public void init(){
    //    //解决netty启动冲突问题
    //    System.setProperty("es.set.netty.runtime.available.processors", "false");
    //}

    public static void main(String[] args) {
        SpringApplication.run(NewCoderApplication.class, args);
    }

}
