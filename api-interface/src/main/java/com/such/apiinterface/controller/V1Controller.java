package com.such.apiinterface.controller;

import com.such.apiinterface.utils.RedirectUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class V1Controller {

    @Resource
    private RedirectUtil redirectUtil;

    @RequestMapping("/v1")
    public String v1(HttpServletRequest request) {
        System.out.println("调试...进入了V1重定向");
        return redirectUtil.redirect(request);
    }

}
