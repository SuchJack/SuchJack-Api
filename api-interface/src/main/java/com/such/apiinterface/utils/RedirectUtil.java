package com.such.apiinterface.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Component
public class RedirectUtil {

    @Resource
    private RequireAllControllerMethodsUtils utils;

    @Resource
    private AuthUtils authUtils;

    @Resource
    private ApplicationContext context;

    public String redirect(HttpServletRequest request) {
        Map<String, String> headers = authUtils.getHeaders(request);

        // 获取请求方法
        String method = request.getMethod();
        // 获取当前请求路径中的类名和方法
        Map<String, String> hashmap = utils.hashmap;
        String url = headers.get("url");
        String key = "[" + url + "]";
        String res = hashmap.get(key);
        if(res == null){
            return null;
        }
        String[] split = res.split("-");
        Object body = null;
        try {
            // 反射构造
            Class<?> forName = Class.forName(split[0]);
            // 由于是object对象，所以实例化对象需要从容器中拿到 todo 优化点：getMethod得到的方法参数数量固定写死，不能自定义
            Method classMethod = forName.getMethod(split[1],String.class);
            System.out.println("调试成功逻辑...1");
            // 调用方法 todo invoke传参固定写死，不能自定义；优化思路：写工具类，从初始化的hashMap载入方法信息
            body = classMethod.invoke(context.getBean(forName), URLDecoder.decode(Objects.requireNonNull(headers.get("body")), StandardCharsets.UTF_8));
            System.out.println("调试成功逻辑...2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(body);
    }
}
