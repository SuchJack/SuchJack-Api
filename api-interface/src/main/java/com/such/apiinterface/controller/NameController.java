package com.such.apiinterface.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.such.apiinterface.utils.RequireAllControllerMethodsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 名称API
 */
@Slf4j
@RestController
@RequestMapping("/name")
public class NameController {

    /**
     * 复读机
     * @param speak 请求参数
     * @return 返回结果
     */
    @GetMapping("/get")
    public String getNameByGet(@RequestParam(name = "request") String speak) {
        if (speak == null || speak.equals("null")) {
            return "你玩我呢？你说话了吗？";
        }

        return "复读:【" + speak + "】...略略略！";
    }

//    @PostMapping("/post")
//    public String getNameByPost(@RequestParam String name, HttpServletRequest request) {
//        String header = request.getHeader("username");
//        if (header == null) {
//            return "header 参数为空";
//        }
//
//        return "POST 你的名字是" + name + "——" + header;
//    }

    /**
     * 获取用户名
     * @param body 前端传入的请求参数
     * @param request 请求信息
     * @return 返回结果
     */
    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody(required = false) String body, HttpServletRequest request) {
        if (StringUtils.isEmpty(body)) {
            return "请求参数不能为空";
        }
//        String body2 = request.getHeader("body"); //  !%7B%22username%22%3A%22%E4%BD%A0%22%7D 乱码！
        String result;
        try {
            JSONObject jsonObject = JSONUtil.parseObj(body);
            result = jsonObject.getStr("username");
        } catch (Exception e) {
            log.error("请求参数格式错误");
            return "请求参数格式错误";
        }
        if (result == null) {
            log.error("请求参数名称错误");
            return "请求参数名称错误";
        }

        return "成功发送POST请求! 你的名字是:【" + result + "】!";
    }

}
