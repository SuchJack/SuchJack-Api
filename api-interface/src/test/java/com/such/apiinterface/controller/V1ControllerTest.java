package com.such.apiinterface.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class V1ControllerTest {

    @Test
    void test(){
        HttpRequest httpRequest = HttpRequest.get("http://localhost:8123/v1/test");
        HttpResponse execute = httpRequest.execute();
        String body = execute.body();
        System.out.println("body = " + body);

    }

}