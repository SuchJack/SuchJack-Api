package com.such.project.controller;

import com.such.apiclientsdk.client.SuchApiClient;
import com.such.apiclientsdk.model.Api;
import com.such.apicommon.model.entity.InterfaceInfo;
import com.such.apicommon.model.entity.User;
import com.such.project.common.BaseResponse;
import com.such.project.common.ErrorCode;
import com.such.project.common.InvokeRequest;
import com.such.project.common.ResultUtils;
import com.such.project.exception.BusinessException;
import com.such.project.model.enums.InterfaceInfoStatusEnum;
import com.such.project.service.InterfaceInfoService;
import com.such.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 接口远程调用
 */
@RestController
@RequestMapping("/invoke")
@Slf4j
public class InvokeController {

    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserService userService;

    /**
     * 通用接口调用
     *
     * @param invokeRequest 请求对象
     * @param request       http请求对象
     * @return 响应对象
     */
    @PostMapping
    public BaseResponse<Object> invokeInterface(@RequestBody InvokeRequest invokeRequest, HttpServletRequest request) {
        // 检查请求对象是否为空或者接口id是否小于等于0
        if (invokeRequest == null || invokeRequest.getId() <= 0 || invokeRequest.getMethod() == null || invokeRequest.getUrl() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取接口id
        Long id = invokeRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 检查接口状态是否为下线状态
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }

        // 获取该接口的url、method，便于传入给sdk使用
        String url = oldInterfaceInfo.getUrl();
        String method = oldInterfaceInfo.getMethod();

        // 获取当前登陆用户的ak和sk，这样相当于用户自己的这个身份去调用
        // 也不会担心它刷接口，因为知道了是谁刷了这个接口，会比较安全
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();

        // 创建一个临时的SuchApiClient对象，并传入ak和sk
        SuchApiClient apiClient = new SuchApiClient(accessKey, secretKey);

        if (invokeRequest.getUserRequestParams() == null) {
            invokeRequest.setUserRequestParams("NULL_PARAM");
        }
        // 测试调用，解析传递过来的值
        Api api = new Api();
        api.setId(loginUser.getId());
        api.setUserAccount(loginUser.getUserAccount());
        api.setInterfaceId(String.valueOf(id));
        api.setUrl(url);
        api.setBody(invokeRequest.getUserRequestParams());
        api.setMethod(method);
        String result = apiClient.getResult(api);

        // 请求方法不是post/get，报错处理
        if (result == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        // interface模拟接口下线，报错进行降级处理
        if (result.contains("Error")) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }

        // 返回成功响应，并包含调用结果
        return ResultUtils.success(result);
    }

}
