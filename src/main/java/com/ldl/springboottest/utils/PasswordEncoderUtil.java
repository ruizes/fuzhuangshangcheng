package com.ldl.springboottest.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

@Component
public class PasswordEncoderUtil {

    private static final Logger logger = LoggerFactory.getLogger(PasswordEncoderUtil.class);
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 密码强度正则：至少8位，包含大小写字母、数字和特殊字符
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    /**
     * 密码加密
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        // 密码强度校验
        if (!isPasswordStrong(rawPassword)) {
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        
        String encodedPassword = passwordEncoder.encode(rawPassword);
        logger.info("Password encrypted successfully");
        return encodedPassword;
    }

    /**
     * 密码匹配验证
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        boolean isMatch = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("Password match check: {}", isMatch);
        return isMatch;
    }

    /**
     * 密码强度校验
     * @param password 密码
     * @return 是否符合强度要求
     */
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            logger.warn("Password is too short");
            return false;
        }
        
        boolean isStrong = pattern.matcher(password).matches();
        if (!isStrong) {
            logger.warn("Password does not meet security requirements: must contain at least 8 characters, including uppercase, lowercase, digit and special character");
        }
        return isStrong;
    }
}