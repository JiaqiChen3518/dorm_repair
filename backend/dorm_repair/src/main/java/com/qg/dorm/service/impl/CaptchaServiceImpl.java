package com.qg.dorm.service.impl;

import com.qg.dorm.common.Result;
import com.qg.dorm.service.CaptchaService;
import com.qg.dorm.util.CaptchaUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    // 验证码存储
    private static final Map<String, String> captchaStore = new ConcurrentHashMap<>();
    // 验证码过期时间，5分钟
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000;
    // 验证码创建时间存储
    private static final Map<String, Long> captchaTimeStore = new ConcurrentHashMap<>();

    @Override
    public Result<String> generateCaptcha(String sessionId) {
        String code = CaptchaUtil.generateCode();
        // 存储验证码到内存, 并关联会话ID，后续验证时需要根据会话ID获取验证码和创建时间进行验证
        captchaStore.put(sessionId, code);
        captchaTimeStore.put(sessionId, System.currentTimeMillis());
        return Result.success(code);
    }

    @Override
    public Result<Boolean> validateCaptcha(String sessionId, String inputCode) {
        String correctCode = captchaStore.get(sessionId);
        Long createTime = captchaTimeStore.get(sessionId);

        if (correctCode == null || createTime == null) {
            return Result.error(400, "验证码不存在或已过期");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - createTime > CAPTCHA_EXPIRE_TIME) {
            captchaStore.remove(sessionId);
            captchaTimeStore.remove(sessionId);
            return Result.error(400, "验证码已过期");
        }

        boolean isValid = CaptchaUtil.validateCode(inputCode, correctCode);
        if (isValid) {
            captchaStore.remove(sessionId);
            captchaTimeStore.remove(sessionId);
        }

        return Result.success(isValid);
    }
}
