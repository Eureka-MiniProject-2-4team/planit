package com.eureka.mp2.team4.planit.auth.security;

import com.eureka.mp2.team4.planit.auth.constants.Messages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlanitAuthenticationProviderTest {
    private final PlanitUserDetailService userDetailService = mock(PlanitUserDetailService.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final PlanitAuthenticationProvider provider = new PlanitAuthenticationProvider(userDetailService, passwordEncoder);

    @Test
    @DisplayName("로그인 성공 시 인증 토큰 반환")
    void authenticate_success() {
        String email = "test@email.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPass";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn(encodedPassword);
        when(userDetails.getAuthorities()).thenReturn(null);

        when(userDetailService.loadUserByUsername(email)).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, rawPassword);

        var result = provider.authenticate(token);

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isEqualTo(userDetails);
    }

    @Test
    @DisplayName("사용자 정보 조회 실패 시 예외 발생")
    void authenticate_userNotFound_throwsException() {
        String email = "notfound@email.com";
        String password = "pw";

        when(userDetailService.loadUserByUsername(email)).thenThrow(new RuntimeException("사용자 없음"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        assertThatThrownBy(() -> provider.authenticate(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("사용자 없음");
    }

    @Test
    @DisplayName("비밀번호 불일치 시 예외 발생")
    void authenticate_wrongPassword_throwsException() {
        String email = "test@email.com";
        String rawPassword = "wrong";
        String encodedPassword = "encoded";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn(encodedPassword);

        when(userDetailService.loadUserByUsername(email)).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, rawPassword);

        assertThatThrownBy(() -> provider.authenticate(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage(Messages.INVALID_CREDENTIALS);

    }

    @Test
    @DisplayName("다른 타입은 false 반환")
    void supports_returnsFalseForOtherTypes() {
        boolean result = provider.supports(String.class); // 아무 관계 없는 타입
        assertThat(result).isFalse();
    }
}