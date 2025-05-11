package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "my-secret-jwt-key-my-secret-jwt-key"); // 최소 32바이트 이상
    }

    @Test
    @DisplayName("유저 정보로 JWT 토큰 생성 및 검증")
    void createAndParseToken_success() {
        UserDto userDto = new UserDto(
                "uuid-1234", "test@example.com", "testuser",
                "hashedPassword", "nickname", UserRole.ROLE_USER,
                null, null, true, "01012345678"
        );

        String token = jwtService.createToken(userDto.getId(),userDto.getRole().name());

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.getUserId(token)).isEqualTo("uuid-1234");
        assertThat(jwtService.getUserRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("잘못된 토큰은 검증 실패")
    void invalidToken_returnsFalse() {
        String invalidToken = "this.is.not.valid.jwt";

        boolean result = jwtService.validateToken(invalidToken);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 검증 실패")
    void expiredToken_returnsFalse() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L); // 유효 시간 1ms로 세팅

        UserDto userDto = new UserDto(
                "uuid-9999", "expired@example.com", "expireduser",
                "pass", "nick", UserRole.ROLE_USER,
                null, null, true, "01000000000"
        );

        String token = jwtService.createToken(userDto.getUsername(),userDto.getRole().name());
        Thread.sleep(10); // 유효 시간 지나도록 대기

        boolean result = jwtService.validateToken(token);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("유효하지 않은 토큰은 예외 발생")
    void getUserId_invalidToken_throws() {
        assertThatThrownBy(() -> jwtService.getUserId("invalid.token"))
                .isInstanceOf(JwtException.class);
    }
}
