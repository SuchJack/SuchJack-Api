package com.such.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 随机彩虹屁API
 */
@RestController
@RequestMapping("/praise")
public class randomComplimentByGet {
    @GetMapping("/random")
    public String getRandomCompliment(@RequestParam(required = false)String format) {
        String[] compliments = {
                "你的笑容是世界上最美的风景。",
                "你是人群中最闪亮的星星。",
                "你真是个天才！",
                "你的眼睛里有星辰大海。",
                "你简直就是我的超级英雄！"
        };
        int index = (int) (Math.random() * compliments.length);
        return compliments[index];
    }
}
