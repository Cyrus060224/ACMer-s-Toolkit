package com.app.utils;

/**
 * 时间格式化工具类
 */
public class TimeUtils {

    /**
     * 将秒数格式化为 HH:mm:ss 格式
     */
    public static String formatSeconds(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private TimeUtils() {
        // 工具类禁止实例化
    }
}
