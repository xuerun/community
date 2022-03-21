package com.rxue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rxue.dao")
public class NewCoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewCoderApplication.class, args);
    }

}
