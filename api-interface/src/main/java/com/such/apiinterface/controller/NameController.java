package com.such.apiinterface.controller;

import com.such.apiclientsdk.model.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 名称API
 */
@RestController
@RequestMapping("/name")
public class NameController {
    @GetMapping("/get")
    public String getNameByGet(String request) {
        if (request.equals("isEmpty")){
            return "你的请求参数为空";
        }
        return "GET 你的名字是你好" + request;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            return "参数不能为空";
        }
        String result = "POST 你的名字是" + user.getUsername();
        return result;
    }

}
