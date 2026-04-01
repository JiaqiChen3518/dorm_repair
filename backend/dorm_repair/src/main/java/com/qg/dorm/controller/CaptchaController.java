package com.qg.dorm.controller;

import com.qg.dorm.common.Result;
import com.qg.dorm.service.CaptchaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/generate")
    public Result<String> generateCaptcha(HttpSession session) {
        String sessionId = session.getId();
        // 生成验证码，与用户会话ID关联
        return captchaService.generateCaptcha(sessionId);
    }
}
