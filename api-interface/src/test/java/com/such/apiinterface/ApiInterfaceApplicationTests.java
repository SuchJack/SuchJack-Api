package com.such.apiinterface;

import com.such.apiclientsdk.client.SuchApiClient;
import com.such.apiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApiInterfaceApplicationTests {
    @Resource
    private SuchApiClient suchApiClient;

//    @Test
//    void contextLoads() {
//        String result = suchApiClient.getNameByGet("jack");
//        User user = new User();
//        user.setUsername("jack666");
//        String userNameByPost = suchApiClient.getUserNameByPost(user);
//        System.out.println(result);
//        System.out.println(userNameByPost);
//    }




}
