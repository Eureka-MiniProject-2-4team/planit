package com.eureka.mp2.team4.planit.auth.filter;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.JwtService;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.eureka.mp2.team4.planit.auth.constants.Constraints.AUTHORIZATION_HEADER;
import static com.eureka.mp2.team4.planit.auth.constants.Constraints.BEARER_PREFIX;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterUnitTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userMapper);

    @Test
    @DisplayName("유효하지 않은 토큰 - INVALID_TOKEN")
    void invalidToken_returns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + "invalidtoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.validateToken("invalidtoken")).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains(INVALID_TOKEN);
    }

    @Test
    @DisplayName("Authorization 헤더만 있는 경우 - INVALID_TOKEN")
    void emptyToken_returns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains(INVALID_TOKEN);
    }

    @Test
    @DisplayName("유효한 토큰이면 SecurityContext 등록")
    void validToken_setsSecurityContext() throws Exception {
        String token = "valid.token.here";
        UserDto mockUser = mock(UserDto.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.getUserId(token)).thenReturn("1234");
        when(userMapper.findById("1234")).thenReturn(mockUser);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isInstanceOf(PlanitUserDetails.class);
    }

    @Test
    @DisplayName("예외 발생 시 401 응답")
    void exceptionDuringTokenHandling_returns401() throws Exception {
        String token = "cause.exception";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.validateToken(token)).thenThrow(new RuntimeException());

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains(INVALID_TOKEN);
    }
}
