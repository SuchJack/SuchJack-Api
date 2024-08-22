package com.such.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 随机头像API
 */
@RestController
@RequestMapping("/avatar")
public class randomAvatarByGet {

    @GetMapping("/random")
    public String getRandomAvatar(@RequestParam(value = "method", required = false, defaultValue = "pc") String method,
                                  @RequestParam(value = "lx", required = false, defaultValue = "c1") String lx,
                                  @RequestParam(value = "format", required = false, defaultValue = "json") String format) {
        // 模拟随机头像URL生成
        String[] avatars = {
                "https://randomuser.me/api/portraits/men/1.jpg",
                "https://randomuser.me/api/portraits/women/2.jpg",
                "https://randomuser.me/api/portraits/lego/3.jpg",
                "https://randomuser.me/api/portraits/men/4.jpg",
                "https://randomuser.me/api/portraits/women/5.jpg"
        };
        int index = (int) (Math.random() * avatars.length);
        String imgUrl = avatars[index];
//        String response = "{\"code\":\"200\",\"imgurl\":\"" + imgUrl + "\",\"width\":\"400\",\"height\":\"400\"}";
//        return format.equals("images") ? "<img src='" + imgUrl + "' alt='Random Avatar' />" : response;
        return imgUrl;
    }
}
