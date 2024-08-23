package com.such.project.constant;

/**
 * 用户常量
 *
 * @author SuchJack
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    // endregion
    /**
     * 用户默认密码
     */
    String USER_DEFAULT_PASSWORD = "12345678";

    /**
     * 盐值，混淆密码
     */
    String SALT = "such-jack-admin";
}
