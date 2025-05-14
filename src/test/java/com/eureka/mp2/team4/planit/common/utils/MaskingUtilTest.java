package com.eureka.mp2.team4.planit.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaskingUtilTest {

    @Test
    @DisplayName("이메일 마스킹 - 일반적인 이메일")
    void maskEmail_normal() {
        String result = MaskingUtil.maskEmail("honggildong@example.com");
        assertEquals("ho*********@example.com", result);
    }

    @Test
    @DisplayName("이메일 마스킹 - 앞자리가 2자")
    void maskEmail_twoChars() {
        String result = MaskingUtil.maskEmail("ho@gmail.com");
        assertEquals("h*@gmail.com", result);
    }

    @Test
    @DisplayName("이메일 마스킹 - 앞자리가 1자")
    void maskEmail_oneChar() {
        String result = MaskingUtil.maskEmail("h@naver.com");
        assertEquals("h@naver.com", result); // 마스킹할 글자가 없음
    }

}