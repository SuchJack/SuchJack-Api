package com.such.apiinterface.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 获取所有接口信息并存入HashMap中
 */
@Slf4j
@Component
public class RequireAllControllerMethodsUtils extends WebApplicationObjectSupport  {

    public Map<String,String> hashmap = new HashMap<>();

    /**
     * 获取所有接口信息
     */
    @Bean
    public void getController() {
        //获取WebApplicationContext，用于获取Bean
        WebApplicationContext webApplicationContext = getWebApplicationContext();
        //获取spring容器中的RequestMappingHandlerMapping
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) webApplicationContext.getBean("requestMappingHandlerMapping");
        //获取应用中所有的请求
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()){
            //1、获取所有的请求路径 todo 在这一步需要同时把方法的参数获取封装，方便反射调用
            RequestMappingInfo requestMappingInfo = entry.getKey();
            PathPatternsRequestCondition patternsCondition = requestMappingInfo.getPathPatternsCondition(); // getPathPatternsCondition 替换了 getPatternsCondition
            if (patternsCondition == null) {
                continue;
            }
            Set<PathPattern> patterns = patternsCondition.getPatterns();
            //value为处理请求信息的方法，即code
            HandlerMethod handlerMethod = entry.getValue();
            //2、获取类
            String type = handlerMethod.getBeanType().getName();
            //3、获取方法
            String method = handlerMethod.getMethod().getName();
            hashmap.put(patterns.toString(),type+"-"+method);
        }
        log.info(hashmap.toString());
    }
}
