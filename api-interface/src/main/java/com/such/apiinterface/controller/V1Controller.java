package com.such.apiinterface.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class V1Controller {

    @Resource
    private NameController nameController;

    @RequestMapping("/v1")
    public String v1(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String header = request.getHeader("url");
        System.out.println("header = " + header);
        System.out.println("requestURI = " + requestURI);
        return "Succeed connected to api-interface v1.";
    }

}
