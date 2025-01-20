package com.such.apigateway;


import cn.hutool.core.text.AntPathMatcher;
import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.apicommon.model.entity.User;
import com.such.apicommon.service.InnerInterfaceInfoService;
import com.such.apicommon.service.InnerUserInterfaceInfoService;
import com.such.apicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.such.apiclientsdk.utils.SignUtils.genSign;
import static com.such.utils.NetUtils.getIp;

/**
 * 全局请求拦截过滤
 * 设计思路：
 *
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    // 不需要登录可以访问
    private static final List<String> PATH_WHITE_LIST = Arrays.asList("/api/apiclient", "/api/user/**");

    //需要登录才能进行访问
    private static final List<String> PATH_LOGIN_LIST = Arrays.asList("/api/userInterfaceInfo/**", "/api/interfaceInfo/**", "/api/auth/**", "/api/oauth/**", "/api/order/**", "/api/alipay/**");

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 拿到请求、响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().toString();

        // 0. 用户发送请求到 API网关
        // 1. 打印请求日志
        logPrint(request);

        //查询用户是否登录时、用户登录等请求，直接放行
        List<Boolean> collect = PATH_WHITE_LIST.stream().map(item -> {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            return antPathMatcher.match(item, path);
        }).toList();
        if (collect.contains(true)) {
            return chain.filter(exchange);
        }

        // 2. 访问控制 - 黑白名单 // todo 暂时不做测试一下，留到最后阶段使用数据库+redis实现
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            // 设置响应状态码为403 Forbidden（禁止访问）
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            // 返回处理完成的响应
//            return response.setComplete();
//        }
        // 3. 用户鉴权（判断ak、sk 是否合法）
        // 从请求头中获取参数
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String interfaceId = headers.getFirst("interfaceId");
        String body = URLDecoder.decode(Objects.requireNonNull(headers.getFirst("body")), StandardCharsets.UTF_8);

        // 实际情况应该是去数据库中查是否已分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
//        if (!"jack".equals(accessKey)) {
//            return handleNoAuth(response);
//        }

        // 直接校验随机数大于1万
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }

        // 时间和当前时间不能超过5分钟
        Long currentTime = System.currentTimeMillis() / 1000; // 毫秒数 / 1000 = 秒数
        final Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }

        // 实际情况中是从数据库中查出 secretKey
        // 从获取到的用户信息中获取用户的密钥
        String secretKey = invokeUser.getSecretKey();
        // 使用获取到的密钥对请求体进行签名
        String serverSign = genSign(body, secretKey);
        // 检查请求中的签名是否为空，或者是否与服务器生成的签名不一致
        if (sign == null || !sign.equals(serverSign)) {
            return handleNoAuth(response);
        }
//        String severSign = genSign(body, "abcdefgh");
//        // 如果生成的签名不一致，则抛出异常，并提示"无权限"
//        if (!sign.equals(severSign)) {
//            return handleNoAuth(response);
//        }

        // 4. 请求的模拟接口是否存在？
        // 从数据库中查询模拟接口是否存在，以及请求方法是否匹配(还可以校验请求参数)
        // 初始化一个 InterfaceInfo 对象，用于存储查询结果
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(interfaceId);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // 5. 请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);
        // 调用成功之后要输入一个响应日志
//        log.info("响应：" + response.getStatusCode());
        // 6. 响应日志
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
        // 7. todo 调用成功，接口调用次数 + 1
//        if (response.getStatusCode() == HttpStatus.OK){
//
//        } else {
//            // 8. 调用失败，返回一个规范的错误码
//            return handleInvokeError(response);
//        }
//
//        return filter;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private void logPrint(ServerHttpRequest request) {
        log.info("=====  {} 请求开始 =====", request.getId());
        log.info("请求路径：" + request.getPath());
        log.info("请求方法：" + request.getMethod());
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = request.getLocalAddress().getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("网关本地地址：" + request.getLocalAddress());
        log.info("请求远程地址：" + request.getRemoteAddress());
        log.info("接口请求IP：" + getIp(request));
        log.info("url:" + request.getURI());
    }

    /**
     * 处理响应(response装饰器)
     *
     * @param exchange 请求对象
     * @param chain 请求链
     * @return Mono<Void>
     */
    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        ServerHttpRequest request = exchange.getRequest();
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            // 判断状态码是否为200 OK(按道理来说们现在没有调用，是拿不到响应码的，对这个保持质疑...)
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口"调用完成之后"，等它返回结果，
                    // 就会调用writeWith方法，我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据，返回一个处理后的响应体
                            // (这里就理解为它在拼接字符串，它把缓冲区的数据取出来，一点一点拼接好)
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 读取响应体的内容并转换为字节数组
                                        // 7. 调用成功，接口调用次数 + 1
                                        try {
                                            boolean b = innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                            log.info("<-------修改接口调用次数：{}", b ? "成功" : "失败");
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        //rspArgs.add(requestUrl);
                                        String data = new String(content, StandardCharsets.UTF_8);//data
                                        sb2.append(data);
                                        // 6. 打印日志
                                        log.info("响应结果：" + data);
                                        log.info("=====  {} 结束 =====", request.getId());
                                        // 将处理后的内容重新包装成DataBuffer并返回
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器,并继续处理(设置response对象为装饰过的)
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 对于非200 OK的请求,直接返回,进行降级处理
            log.info("=====  {} 结束 =====", request.getId());
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            log.info("=====  {} 结束 =====", request.getId());
            return chain.filter(exchange);
        }
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}

