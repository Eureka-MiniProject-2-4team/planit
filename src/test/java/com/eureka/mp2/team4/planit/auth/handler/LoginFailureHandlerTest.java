package com.eureka.mp2.team4.planit.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.BAD_LOGIN_REQUEST;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_CREDENTIALS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class LoginFailureHandlerTest {
    private final LoginFailureHandler handler = new LoginFailureHandler();

    @Test
    @DisplayName("BAD_LOGIN_REQUEST 발생 시 400 응답")
    public void badRequest_returns400() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationException(BAD_LOGIN_REQUEST) {
        };
        StringWriter out = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        handler.onAuthenticationFailure(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertThat(out.toString()).contains(BAD_LOGIN_REQUEST);
    }

    @Test
    @DisplayName("INVALID_CREDENTIALS 발생 시 401 응답")
    void unauthorized_returns401() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationException(INVALID_CREDENTIALS) {
        };
        StringWriter out = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        handler.onAuthenticationFailure(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(out.toString()).contains(INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("기타 예외 발생 시 500 응답")
    void otherException_returns500() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationException("Unknown error") {
        };
        StringWriter out = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        handler.onAuthenticationFailure(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertThat(out.toString()).contains("Unknown error");
    }

}