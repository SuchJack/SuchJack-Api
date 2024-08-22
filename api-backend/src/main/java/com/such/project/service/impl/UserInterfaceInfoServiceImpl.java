package com.such.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.such.apicommon.model.entity.UserInterfaceInfo;
import com.such.project.common.ErrorCode;
import com.such.project.exception.BusinessException;
import com.such.project.exception.ThrowUtils;
import com.such.project.mapper.UserInterfaceInfoMapper;
import com.such.project.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;

/**
 * @author R7 2700x
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2024-07-25 10:12:08
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0, ErrorCode.PARAMS_ERROR);
        }

        // 有参数则校验
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }

    }

    // todo 添加事务和锁，防止用户瞬间调用大量接口次数、统计出错（参考伙伴匹配系统）
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        // 判断(其实这里还应该校验存不存在，这里就不用校验了，因为它不存在，也更新不到那条记录)
        if (interfaceInfoId <= 0 || userId <= 0){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 使用 UpdateWrapper 对象来构建更新条件
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        // 在 updateWrapper 中设置了两个条件：interfaceInfoId 等于给定的 interfaceInfoId 和 userId 等于给定的 userId。
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        // setSql 方法用于设置要更新的 SQL 语句。这里通过 SQL 表达式实现了两个字段的更新操作:
        // leftNum = leftNum - 1 和 totalNum = totalNum + 1 。意思是将 leftNum 字段减一，totalNum字段加一。
        updateWrapper.setSql("leftNum = leftNum - 1,totalNum = totalNum + 1");
        // 最后，调用 update 方法执行更新操作，并返回更新是否成功的结果
        return this.update(updateWrapper);
    }
}




