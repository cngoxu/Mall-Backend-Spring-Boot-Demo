package xyz.cngo.common.utils;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

public class GenerateUtil {
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 生成六位随机验证码（包含大小写字母和数字）
     * @return 验证码字符串
     */
    public static String generateVerificationCode() {
        // 定义字符集（包含大小写字母和数字）
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();

        // 生成六位验证码
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }

        return code.toString();
    }

    /**
     * 生成流水号
     * 格式：类型 + 日期时间戳（14位）+ 4位随机数
     * @param type 流水号类型（如 "OD" 表示订单，"ST" 表示库存，"RP" 表示充值）
     * @return 生成的流水号
     */
    public static String generateSerialNumber(String type) {
        // 获取当前时间的日期时间戳
        String timestamp = dateFormat.format(new Date());
        // 生成4位随机数
        String randomSuffix = String.format("%04d", random.nextInt(10000));
        // 拼接流水号
        return type + timestamp + randomSuffix;
    }

    public static String generateStockSerialNumber() {
        return generateSerialNumber("ST");
    }

    public static String generateOrderSerialNumber() {
        return generateSerialNumber("OD");
    }

    public static String generateBalanceSerialNumber() {
        return GenerateUtil.generateSerialNumber("RP");
    }
}
