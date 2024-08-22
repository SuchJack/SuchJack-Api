package com.such.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.such.apicommon.model.entity.User;
import com.such.apicommon.service.InnerUserService;
import com.such.project.common.ErrorCode;
import com.such.project.exception.BusinessException;
import com.such.project.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 实现接口中的 getInvokeUser 方法，用于根据密钥获取内部用户信息
     * @param accessKey 密钥
     * @return 内部用户信息，如果找不到匹配的用户则返回 null
     * @throws BusinessException 参数错误时抛出业务异常
     */
    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey",accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
