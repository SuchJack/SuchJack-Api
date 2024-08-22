package com.such.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 随机鸡汤API
 */
@RestController
@RequestMapping("/soup")
public class randomSoupByGet {

    @GetMapping("/random")
    public String getRandomSoup(@RequestParam(value = "charset", required = false, defaultValue = "utf-8") String charset,
                                @RequestParam(value = "encode", required = false, defaultValue = "text") String encode) {
        // 模拟随机鸡汤句子生成
        String[] soups = {
                "生活总是充满了惊喜和挑战。",
                "坚持就是胜利。",
                "每一个不曾起舞的日子，都是对生命的辜负。",
                "把每一天当作生命的最后一天来过。",
                "心有多大，舞台就有多大。"
        };
        int index = (int) (Math.random() * soups.length);
        return encode.equals("json") ? "{\"text\": \"" + soups[index] + "\"}" : soups[index];
    }
}
