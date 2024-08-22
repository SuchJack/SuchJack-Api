package com.such.project.model.dto.interfaceInfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用接口请求
 *
 * @author SuchJack
 * @from 漠上鸿网络
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

}
