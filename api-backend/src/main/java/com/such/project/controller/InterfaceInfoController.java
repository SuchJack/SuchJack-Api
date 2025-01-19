package com.such.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.such.apiclientsdk.client.SuchApiClient;
import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.apicommon.model.entity.User;
import com.such.project.annotation.AuthCheck;
import com.such.project.common.*;
import com.such.project.constant.CommonConstant;
import com.such.project.constant.UserConstant;
import com.such.project.exception.BusinessException;
import com.such.project.exception.ThrowUtils;
import com.such.project.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.such.project.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.such.project.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.such.project.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.such.project.model.enums.InterfaceInfoStatusEnum;
import com.such.project.service.InterfaceInfoService;
import com.such.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private SuchApiClient suchApiClient;

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        // 校验非空
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 属性拷贝
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);

        // 校验所有参数非空
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);

        // 获取当前用户
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());

        // 数据库插入接口
        boolean result = interfaceInfoService.save(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 回显id并返回创建成功的id
        long newInterfaceInfoId = interfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 校验删除操作的用户是否为空
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前用户
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 返回删除成功的结果
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param interfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        // 判断请求更新的参数是否异常(非空且是正常用户)
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 属性拷贝
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();

        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可修改
        ThrowUtils.throwIf(!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request),ErrorCode.NO_AUTH_ERROR);

        // 返回修改成功的结果
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        // 判断参数id是否合法
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 判断是否存在该id的接口
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        // 判断请求参数，属性拷贝
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null){
            BeanUtils.copyProperties(interfaceInfoQueryRequest,interfaceInfoQuery);
        }

        // mp的qw查询
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        // 判断请求参数是否为空
        ThrowUtils.throwIf(interfaceInfoQueryRequest == null,ErrorCode.PARAMS_ERROR);

        // 属性拷贝
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest,interfaceInfoQuery);

        // 获取PageRequest参数
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQueryRequest.getDescription();

        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);

        // 限制爬虫
        ThrowUtils.throwIf(size > 50,ErrorCode.PARAMS_ERROR);

        // mp分页查询 - qw设置
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description),"description",description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),sortOrder
                .equals(CommonConstant.SORT_ORDER_ASC),sortField);

        // mp分页查询 - 执行
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }


    /**
     * 发布（仅管理员）
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1.校验该接口是否存在
        // 获取idRequest对象的id属性值
        Long id = idRequest.getId();
        // 根据id查询接口信息数据
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        // 如果查询结果为空
        if (oldInterfaceInfo == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 2.判断该接口是否可以调用
//        com.such.apiclientsdk.model.User user = new com.such.apiclientsdk.model.User();
//        user.setUsername("test");
        // todo 发布时要测试接口是否能调用
//        String username = suchApiClient.getUserNameByPost(user);
//        if (StringUtils.isBlank(username)){
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"接口验证失败");
//        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        // 3.修改接口数据库中的状态字段为上线
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线（仅管理员）
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                      HttpServletRequest request) {
        if(idRequest == null || idRequest.getId() <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.校验该接口是否存在
        // 获取idRequest对象的id属性值
        Long id = idRequest.getId();
        // 根据id查询接口信息数据
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        // 如果查询结果为空
        if (oldInterfaceInfo == null) {
            // 抛出异常
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        // 3.修改接口数据库中的状态字段为上线
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

//    /**
//     * 测试调用
//     *
//     * @param interfaceInfoInvokeRequest
//     * @param request
//     * @return
//     */
//    //
//    @PostMapping("/invoke")
//    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
//                                                      HttpServletRequest request) {
//        // 检查请求对象是否为空或者接口id是否小于等于0
//        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0 ){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        // 获取接口id
//        Long id = interfaceInfoInvokeRequest.getId();
//        // 获取用户请求参数
//        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        if(oldInterfaceInfo == null){
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
//        }
//        // 检查接口状态是否为下线状态
//        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口已关闭");
//        }
//
//        // 获取当前登陆用户的ak和sk，这样相当于用户自己的这个身份去调用
//        // 也不会担心它刷接口，因为知道了是谁刷了这个接口，会比较安全
//        User loginUser = userService.getLoginUser(request);
//        String accessKey = loginUser.getAccessKey();
//        String secretKey = loginUser.getSecretKey();
//        // 创建一个临时的SuchApiClient对象，并传入ak和sk
//        SuchApiClient tempClient = new SuchApiClient(accessKey, secretKey);
//        // 测试调用，解析传递过来的值
//        Gson gson = new Gson();
//        // 将用户请求参数转换为com.such.apiclientsdk.model.User对象
//        com.such.apiclientsdk.model.User user = gson.fromJson(userRequestParams,com.such.apiclientsdk.model.User.class);
//        // 调用SuchApiClient的getUsernameByPost方法，传入用户对象，获取用户名
////        String userNameByPost = temClient.getUserNameByPost(user);
//        // 返回成功响应，并包含调用结果
////        if (userNameByPost.contains("Error")){
////            return ResultUtils.error(0,"请求失败");
////        }
//
//        return ResultUtils.success(null);
//    }

//    /**
//     * 分页获取列表（封装类）
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/list/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long size = interfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
//                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
//    }
//
//    /**
//     * 分页获取当前用户创建的资源列表
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/my/list/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        if (interfaceInfoQueryRequest == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        User loginUser = userService.getLoginUser(request);
//        interfaceInfoQueryRequest.setUserId(loginUser.getId());
//        long current = interfaceInfoQueryRequest.getCurrent();
//        long size = interfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
//                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
//    }
//
//    // endregion
//
//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     *
//     * @param interfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> searchInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = interfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.searchFromEs(interfaceInfoQueryRequest);
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
//    }
//
//    /**
//     * 编辑（用户）
//     *
//     * @param interfaceInfoEditRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest interfaceInfoEditRequest, HttpServletRequest request) {
//        if (interfaceInfoEditRequest == null || interfaceInfoEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo interfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(interfaceInfoEditRequest, interfaceInfo);
//        List<String> tags = interfaceInfoEditRequest.getTags();
//        if (tags != null) {
//            interfaceInfo.setTags(JSONUtil.toJsonStr(tags));
//        }
//        // 参数校验
//        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = interfaceInfoEditRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = interfaceInfoService.updateById(interfaceInfo);
//        return ResultUtils.success(result);
//    }

}
