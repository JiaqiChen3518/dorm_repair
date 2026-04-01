package com.qg.dorm.util;

import java.util.Random;

public class CaptchaUtil {
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 4;
    private static final Random random = new Random();

    // 生成验证码
    public static String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    // 验证验证码是否正确，不区分大小写
    public static boolean validateCode(String inputCode, String correctCode) {
        if (inputCode == null || correctCode == null) {
            return false;
        }
        return inputCode.equalsIgnoreCase(correctCode);
    }
}
