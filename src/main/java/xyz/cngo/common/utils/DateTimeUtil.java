package xyz.cngo.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateTimeUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Integer VerficationCodeExpireTime = 10;

    public static boolean isVerficationCodeExpired(String createTimeStr){
        if (Objects.isNull(createTimeStr) || createTimeStr.isEmpty()) {
            return true;
        }
        try {
            LocalDateTime createTime = LocalDateTime.parse(createTimeStr, FORMATTER);
            LocalDateTime now = LocalDateTime.now();
            // 计算时间差并比较
            return Duration.between(createTime, now).toMinutes() > VerficationCodeExpireTime;
        } catch (Exception e) {
            // 解析失败视为过期
            return true;
        }
    }

    public static String getNowTimeStr(){
        return LocalDateTime.now(ZoneOffset.of("+8")).format(FORMATTER);
    }
}
