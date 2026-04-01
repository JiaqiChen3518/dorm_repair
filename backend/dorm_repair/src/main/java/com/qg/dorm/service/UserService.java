package com.qg.dorm.service;

import com.qg.dorm.common.Result;
import com.qg.dorm.entity.User;

public interface UserService {
    /**
     * 注册用户
     * @param account
     * @param password
     * @param role
     * @param captcha
     * @param sessionId
     * @return
     */
    Result<User> register(String account, String password, Integer role, String captcha, String sessionId);

    /**
     * 用户登录
     * @param account
     * @param password
     * @param captcha
     * @param sessionId
     * @return
     */
    Result<User> login(String account, String password, String captcha, String sessionId);

    /**
     * 更新用户密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Result<Void> updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 绑定宿舍
     * @param userId
     * @param building
     * @param room
     * @return
     */
    Result<Void> bindDormitory(Long userId, String building, String room);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    Result<User> getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Result<Void> updateUserInfo(User user);
}
