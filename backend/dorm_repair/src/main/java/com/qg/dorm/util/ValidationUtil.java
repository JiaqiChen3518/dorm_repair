package com.qg.dorm.util;

import com.qg.dorm.constant.RoleConstant;

import java.util.regex.Pattern;

/**
 * 验证工具类
 * 验证账号格式
 */
public class ValidationUtil {
    // 学生账号正则表达式
    private static final Pattern STUDENT_PATTERN = Pattern.compile("(3125|3225)\\d{6}");
    // 管理员账号正则表达式
    private static final Pattern ADMIN_PATTERN = Pattern.compile("0025\\d{6}");

    public static boolean isValidStudentAccount(String account) {
        return STUDENT_PATTERN.matcher(account).matches();
    }

    public static boolean isValidAdminAccount(String account) {
        return ADMIN_PATTERN.matcher(account).matches();
    }

    public static boolean isValidAccount(String account, int role) {
        if (role == RoleConstant.ROLE_STUDENT) {
            return isValidStudentAccount(account);
        } else if (role == RoleConstant.ROLE_ADMIN) {
            return isValidAdminAccount(account);
        }
        return false;
    }
}
