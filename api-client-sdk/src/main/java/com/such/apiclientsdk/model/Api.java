package com.such.apiclientsdk.model;

import lombok.Data;

/**
 * 请求API封装
 *
 * @author SuchJack
 */
@Data
public class Api {
    /**
     * 用户id
     */
    Long id;
    /**
     * 用户账号
     */
    String userAccount;
    /**
     * 接口id
     */
    String interfaceId;
    /**
     * 请求地址
     */
    String url;
    /**
     * 请求体
     */
    Object body;
    /**
     * 请求方法
     */
    String method;
}
