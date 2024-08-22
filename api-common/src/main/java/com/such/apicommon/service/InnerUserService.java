package com.such.apicommon.service;


import com.such.apicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author SuchJack
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户密钥(accessKey)
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
