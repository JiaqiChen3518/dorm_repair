package com.qg.dorm.constant;

/**
 * 设备类型常量类
 */
public class DeviceTypeConstant {
    public static final int DEVICE_FAUCET = 1;
    public static final int DEVICE_LIGHT = 2;
    public static final int DEVICE_DOOR_WINDOW = 3;
    public static final int DEVICE_AC = 4;
    public static final int DEVICE_HEATER = 5;
    public static final int DEVICE_OTHER = 6;

    public static String getTypeText(int type) {
        return switch (type) {
            case DEVICE_FAUCET -> "水龙头";
            case DEVICE_LIGHT -> "电灯";
            case DEVICE_DOOR_WINDOW -> "门窗";
            case DEVICE_AC -> "空调";
            case DEVICE_HEATER -> "热水器";
            case DEVICE_OTHER -> "其他";
            default -> "未知";
        };
    }
}
