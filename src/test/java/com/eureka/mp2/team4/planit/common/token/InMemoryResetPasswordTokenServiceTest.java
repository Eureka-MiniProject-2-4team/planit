package com.eureka.mp2.team4.planit.common.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryResetPasswordTokenServiceTest {
    private InMemoryResetPasswordTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new InMemoryResetPasswordTokenService();
    }

    @Test
    @DisplayName("토큰 저장 후 조회 성공")
    void testSaveAndGetToken() {
        String token = "abc123";
        String userId = "user-id-1";

        tokenService.saveToken(token, userId);

        String result = tokenService.getUserIdByToken(token);
        assertEquals(userId, result);
    }

    @Test
    @DisplayName("토큰 삭제 후 조회하면 null 반환")
    void testRemoveToken() {
        String token = "abc456";
        String userId = "user-id-2";

        tokenService.saveToken(token, userId);
        tokenService.removeToken(token);

        assertNull(tokenService.getUserIdByToken(token));
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 null 반환")
    void testGetInvalidToken() {
        assertNull(tokenService.getUserIdByToken("non-existent-token"));
    }
}