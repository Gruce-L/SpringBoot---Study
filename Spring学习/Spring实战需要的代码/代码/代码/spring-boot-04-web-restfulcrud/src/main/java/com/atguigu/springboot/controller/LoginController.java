package com.atguigu.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {
    //处理删除，获取，推送请求
//    @DeleteMapping
//    @PutMapping
//    @GetMapping

    //@RequestMapping(value = "/user/login",method = RequestMethod.POST)
    @PostMapping(value = "/user/login")
    //从参数上提交两个数据，一个是用户名，一个是密码
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String,Object> map, HttpSession session){
        if(!StringUtils.isEmpty(username) && "123456".equals(password)){
            //登陆成功，防止表单重复提交，可以重定向到主页
            session.setAttribute("loginUser",username);
            return "redirect:/main.html";
        }else{
            //登陆失败
            //回到登陆页面，并添加错误消息，使用map方法添加错误消息
            map.put("msg","用户名密码错误");
            return  "login";
        }

    }
}
