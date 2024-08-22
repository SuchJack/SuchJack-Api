package com.such.project.common;

import lombok.Data;

/**
 * 调用接口请求
 *
 * @author SuchJack
 * @from 漠上鸿网络
 */
@Data
public class InvokeRequest {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求方法
     */
    private String method;

    /**
     * 用户请求参数
     */
    private String userRequestParams = "isEmpty";
}
