package com.qg.dorm.controller;

import com.qg.dorm.common.Result;
import com.qg.dorm.dto.BindDormitoryDTO;
import com.qg.dorm.dto.LoginDTO;
import com.qg.dorm.dto.UpdatePasswordDTO;
import com.qg.dorm.dto.UserDTO;
import com.qg.dorm.entity.User;
import com.qg.dorm.service.UserService;
import com.qg.dorm.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody UserDTO dto, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        return userService.register(dto.getAccount(), dto.getPassword(), dto.getRole(), dto.getCaptcha(), sessionId);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        Result<User> result = userService.login(dto.getAccount(), dto.getPassword(), dto.getCaptcha(), sessionId);

        if (result.getCode() == 200) {
            User user = result.getData();

            // 生成JWT
            String token = JwtUtil.generateToken(user.getId(), user.getAccount(), user.getRole());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("id", user.getId());
            data.put("account", user.getAccount());
            data.put("role", user.getRole());
            data.put("name", user.getName());
            data.put("isBound", user.getIsBound());
            data.put("building", user.getBuilding());
            data.put("room", user.getRoom());
            return Result.success(data);
        }
        return Result.error(result.getMsg());
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserInfo(userId);
    }

    @PostMapping("/updatePassword")
    public Result<Void> updatePassword(@RequestBody UpdatePasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.updatePassword(userId, dto.getOldPassword(), dto.getNewPassword());
    }

    @PostMapping("/bindDormitory")
    public Result<Void> bindDormitory(@RequestBody BindDormitoryDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.bindDormitory(userId, dto.getBuilding(), dto.getRoom());
    }

    @PostMapping("/updateInfo")
    public Result<Void> updateInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        return userService.updateUserInfo(user);
    }
}
