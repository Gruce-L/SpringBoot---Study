package com.atguigu.springboot01helloworldquick.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//ResponseBody写在这里表明所有方法返回的数值直接写给浏览器，（如果是对象转为json数据）
/*@ResponseBody
@Controller*/

//优化方法：==@ResponseBody + @Controller
@RestController
public class HelloController {
    //发送一个Hello请求

    //字符串写给浏览器
    //@ResponseBody
    //界面上要发送一个Hello请求
    @RequestMapping("/hello")
    public String hello(){
        return "hello world quick!";
    }

    //RESTAPI的方式
}
