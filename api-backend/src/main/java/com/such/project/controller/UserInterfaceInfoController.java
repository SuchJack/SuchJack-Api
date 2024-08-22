package com.such.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.such.apicommon.model.entity.User;
import com.such.apicommon.model.entity.UserInterfaceInfo;
import com.such.project.annotation.AuthCheck;
import com.such.project.common.BaseResponse;
import com.such.project.common.DeleteRequest;
import com.such.project.common.ErrorCode;
import com.such.project.common.ResultUtils;
import com.such.project.constant.UserConstant;
import com.such.project.exception.BusinessException;
import com.such.project.exception.ThrowUtils;
import com.such.project.model.dto.userInterfaceInfo.UserInterfaceInfoAddRequest;
import com.such.project.model.dto.userInterfaceInfo.UserInterfaceInfoQueryRequest;
import com.such.project.model.dto.userInterfaceInfo.UserInterfaceInfoUpdateRequest;
import com.such.project.service.UserInterfaceInfoService;
import com.such.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口管理
 *
 * @author SuchJack
 * @from 漠上鸿网络
 */
@RestController
@RequestMapping("/userUserInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userUserInterfaceInfoService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param userUserInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userUserInterfaceInfoAddRequest, HttpServletRequest request) {
        // 校验非空
        if (userUserInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 属性拷贝
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoAddRequest, userUserInterfaceInfo);

        // 校验所有参数非空
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, true);

        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        userUserInterfaceInfo.setUserId(loginUser.getId());

        // 数据库插入接口
        boolean result = userUserInterfaceInfoService.save(userUserInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 回显id并返回创建成功的id
        long newUserInterfaceInfoId = userUserInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 校验删除操作的用户是否为空
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前用户
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 返回删除成功的结果
        boolean b = userUserInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param userUserInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest userUserInterfaceInfoUpdateRequest,
                                                         HttpServletRequest request) {
        // 判断请求更新的参数是否异常(非空且是正常用户)
        if (userUserInterfaceInfoUpdateRequest == null || userUserInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 属性拷贝
        UserInterfaceInfo userUserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userUserInterfaceInfoUpdateRequest, userUserInterfaceInfo);

        // 参数校验
        userUserInterfaceInfoService.validUserInterfaceInfo(userUserInterfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = userUserInterfaceInfoUpdateRequest.getId();

        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可修改
        ThrowUtils.throwIf(!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request), ErrorCode.NO_AUTH_ERROR);

        // 返回修改成功的结果
        boolean result = userUserInterfaceInfoService.updateById(userUserInterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)

    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoVOById(long id, HttpServletRequest request) {
        // 判断参数id是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在该id的接口
        UserInterfaceInfo userUserInterfaceInfo = userUserInterfaceInfoService.getById(id);
        if (userUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(userUserInterfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param userUserInterfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list")
    public BaseResponse<List<UserInterfaceInfo>> listUserInterfaceInfo(@RequestBody UserInterfaceInfoQueryRequest userUserInterfaceInfoQueryRequest) {
        // 判断请求参数，属性拷贝
        UserInterfaceInfo userUserInterfaceInfoQuery = new UserInterfaceInfo();
        if (userUserInterfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(userUserInterfaceInfoQueryRequest, userUserInterfaceInfoQuery);
        }

        // mp的qw查询
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>(userUserInterfaceInfoQuery);
        List<UserInterfaceInfo> userUserInterfaceInfoList = userUserInterfaceInfoService.list(queryWrapper);
        return ResultUtils.success(userUserInterfaceInfoList);
    }


}
