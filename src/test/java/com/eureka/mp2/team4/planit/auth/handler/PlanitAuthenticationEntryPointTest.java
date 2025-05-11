package com.eureka.mp2.team4.planit.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.NEED_LOGIN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class PlanitAuthenticationEntryPointTest {
    private PlanitAuthenticationEntryPoint authenticationEntryPoint = new PlanitAuthenticationEntryPoint();

    @Test
    @DisplayName("로그인 하지 않고 api 요청시 401응답")
    public void forbiddenRequest_returns401() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationServiceException(NEED_LOGIN);


        StringWriter out = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        authenticationEntryPoint.commence(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(out.toString()).contains(NEED_LOGIN);
    }

}