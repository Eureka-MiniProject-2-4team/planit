package com.eureka.mp2.team4.planit.common.utils;

public class MaskingUtil {
    public static String maskEmail(String email) {
        int atIdx = email.indexOf("@");
        if (atIdx <= 2) {
            return email.charAt(0) + "*".repeat(atIdx - 1) + email.substring(atIdx);
        }

        String prefix = email.substring(0, 2);
        String masked = "*".repeat(atIdx - 2);
        String domain = email.substring(atIdx);
        return prefix + masked + domain;
    }
}
