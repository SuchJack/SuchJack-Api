package com.such.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.apicommon.service.InnerInterfaceInfoService;
import com.such.project.common.ErrorCode;
import com.such.project.exception.BusinessException;
import com.such.project.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 查看模拟接口是否存在
     * @param interfaceId
     * @return
     */
    @Override
    public InterfaceInfo getInterfaceInfo(String interfaceId) {
        if (StringUtils.isAnyBlank(interfaceId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",interfaceId);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
