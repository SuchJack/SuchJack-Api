package com.such.apicommon.service;


/**
* @author R7 2700x
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2024-07-25 10:12:08
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);
}
