package com.such.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.project.common.ErrorCode;
import com.such.project.exception.BusinessException;
import com.such.project.exception.ThrowUtils;
import com.such.project.mapper.InterfaceInfoMapper;
import com.such.project.service.InterfaceInfoService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author R7 2700x
* @description 针对表【interface_info(接口信息)】的数据库操作Service实现
* @createDate 2024-07-23 11:17:55
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {

        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name), ErrorCode.PARAMS_ERROR);
        }

        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }

    }
}




