package com.wlinsk.ansure.basic.utils;

import java.security.SecureRandom;

/**
 * @author wlinsk
 * @date 2025/9/13
 */
public class VerifyCodeUtils {
    private static final SecureRandom random = new SecureRandom();

    public static String generateNumericCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
