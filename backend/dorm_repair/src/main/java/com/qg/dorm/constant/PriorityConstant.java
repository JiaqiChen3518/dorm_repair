package com.qg.dorm.constant;


/**
 * 优先级常量类
 */
public class PriorityConstant {
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_LOW = 3;

    public static String getPriorityText(int priority) {
        return switch (priority) {
            case PRIORITY_HIGH -> "紧急";
            case PRIORITY_NORMAL -> "普通";
            case PRIORITY_LOW -> "低";
            default -> "未知";
        };
    }
}
