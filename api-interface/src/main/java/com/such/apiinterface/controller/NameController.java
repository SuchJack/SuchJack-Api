package com.such.apiinterface.controller;

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
        return "测试调用结果：" + request;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody String body, HttpServletRequest request) {
        if (body == null) {
            return "参数不能为空";
        }
        String result = "POST 你的名字是" + body;
        return result;
    }

}
