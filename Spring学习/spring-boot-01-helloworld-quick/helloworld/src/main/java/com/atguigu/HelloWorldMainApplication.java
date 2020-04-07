package com.atguigu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//需要让idea知道这是一个springboot程序，需要加入springboot注解
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {
        //Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class, args);
    }
}
