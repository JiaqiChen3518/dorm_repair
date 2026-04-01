package com.qg.dorm.service;

import com.qg.dorm.common.Result;
import java.util.Map;

public interface CaptchaService {
    /**
     * 生成验证码
     * @param sessionId
     * @return
     */
    Result<String> generateCaptcha(String sessionId);

    /**
     * 验证验证码
     * @param sessionId
     * @param inputCode
     * @return
     */
    Result<Boolean> validateCaptcha(String sessionId, String inputCode);
}
