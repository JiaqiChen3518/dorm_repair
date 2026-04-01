package com.qg.dorm.service.impl;

import com.qg.dorm.common.Result;
import com.qg.dorm.constant.RoleConstant;
import com.qg.dorm.entity.User;
import com.qg.dorm.mapper.UserMapper;
import com.qg.dorm.service.CaptchaService;
import com.qg.dorm.service.UserService;
import com.qg.dorm.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CaptchaService captchaService;

    @Override
    public Result<User> register(String account, String password, Integer role, String captcha, String sessionId) {
        Result<Boolean> captchaResult = captchaService.validateCaptcha(sessionId, captcha);
        if (captchaResult.getCode() != 200 || !captchaResult.getData()) {
            return Result.error("验证码错误");
        }

        if (!ValidationUtil.isValidAccount(account, role)) {
            return Result.error("账号格式不正确");
        }

        if (userMapper.selectByAccount(account) != null) {
            return Result.error("账号已存在");
        }

        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(1); // 默认启用
        user.setIsBound(0); // 设置为未绑定宿舍

        userMapper.insert(user);
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<User> login(String account, String password, String captcha, String sessionId) {
        Result<Boolean> captchaResult = captchaService.validateCaptcha(sessionId, captcha);
        if (captchaResult.getCode() != 200 || !captchaResult.getData()) {
            return Result.error("验证码错误");
        }

        User user = userMapper.selectByAccount(account);
        if (user == null) {
            return Result.error("账号或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            return Result.error("账号或密码错误");
        }

        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<Void> updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!oldPassword.equals(user.getPassword())) {
            return Result.error("原密码错误");
        }

        userMapper.updatePassword(userId, newPassword);
        return Result.success();
    }

    @Override
    public Result<Void> bindDormitory(Long userId, String building, String room) {
        if (building == null || building.trim().isEmpty() || room == null || room.trim().isEmpty()) {
            return Result.error("宿舍楼栋和房间号不能为空");
        }
        userMapper.bindDormitory(userId, building.trim(), room.trim());
        return Result.success();
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<Void> updateUserInfo(User user) {
        userMapper.updateInfo(user);
        return Result.success();
    }
}
