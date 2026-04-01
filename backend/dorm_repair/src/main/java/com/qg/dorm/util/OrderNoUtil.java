package com.qg.dorm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单号工具类
 * 生成订单号
 */
public class OrderNoUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateOrderNo() {
        String timeStr = LocalDateTime.now().format(formatter);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "R" + timeStr + random;
    }
}
