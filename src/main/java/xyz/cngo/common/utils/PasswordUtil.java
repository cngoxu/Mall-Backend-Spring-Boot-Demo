package xyz.cngo.common.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * 对密码进行加密
     * @param plainPassword 明文密码
     * @return 加密后的密码哈希值
     */
    public static String hashPassword(String plainPassword) {
        // 使用 bcrypt 算法对密码进行加密
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码是否正确
     * @param plainPassword 用户输入的明文密码
     * @param hashedPassword 存储在数据库中的哈希值
     * @return 如果密码匹配返回 true，否则返回 false
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // 使用 bcrypt 算法验证密码
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}