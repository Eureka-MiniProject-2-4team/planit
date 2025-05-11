package com.eureka.mp2.team4.planit.auth.filter;

import com.eureka.mp2.team4.planit.auth.dto.request.UserLoginDto;
import com.eureka.mp2.team4.planit.auth.handler.LoginFailureHandler;
import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.JwtService;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.BAD_LOGIN_REQUEST;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_CREDENTIALS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class PlanitLoginFilterTest {
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final PlanitLoginFilter loginFilter = new PlanitLoginFilter(authenticationManager, jwtService);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("로그인 성공 시 JWT 토큰 포함 응답")
    void successLogin_returnsToken() throws Exception {
        UserLoginDto dto = new UserLoginDto("test@email.com", "qwer1234@");
        String token = "jwt.token";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(objectMapper.writeValueAsBytes(dto));
        request.setContentType("application/json");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        UserDto userDto = mock(UserDto.class);
        PlanitUserDetails userDetails = new PlanitUserDetails(userDto);
        Authentication authResult = mock(Authentication.class);
        when(authResult.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authResult);
        when(jwtService.createToken(any())).thenReturn(token);

        loginFilter.successfulAuthentication(request, response, chain, authResult);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일 또는 비밀번호")
    void wrongCredentials_returns401() throws Exception {
        UserLoginDto dto = new UserLoginDto("wrong@email.com", "wrongpass");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(objectMapper.writeValueAsBytes(dto));
        request.setContentType("application/json");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new BadCredentialsException(INVALID_CREDENTIALS))
                .when(authenticationManager).authenticate(any());

        try {
            loginFilter.attemptAuthentication(request, response);
        } catch (AuthenticationException ignored) {
        }

        LoginFailureHandler handler = new LoginFailureHandler();
        handler.onAuthenticationFailure(request, response, new BadCredentialsException(INVALID_CREDENTIALS));

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains(INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("로그인 요청 파싱 실패 - BAD_LOGIN_REQUEST")
    void wrongFormat_returns400() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent("{\"id\":\"email\", \"password\":\"pass\"}".getBytes());
        request.setContentType("application/json");

        MockHttpServletResponse response = new MockHttpServletResponse();

        try {
            loginFilter.attemptAuthentication(request, response);
        } catch (AuthenticationException e) {
            LoginFailureHandler handler = new LoginFailureHandler();
            handler.onAuthenticationFailure(request, response, e);

            assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
            assertThat(response.getContentAsString()).contains(BAD_LOGIN_REQUEST);
        }
    }

}