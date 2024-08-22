package com.such.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.such.apicommon.model.entity.InterfaceInfo;

/**
* @author R7 2700x
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2024-07-23 11:17:55
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
