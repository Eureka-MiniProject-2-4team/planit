package com.eureka.mp2.team4.planit.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.eureka.mp2.team4.planit.common.Message.FORBIDDEN;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class PlanitAccessDeniedHandlerTest {
    private PlanitAccessDeniedHandler accessDeniedHandler = new PlanitAccessDeniedHandler();

    @Test
    @DisplayName("권한이 없는 api 요청시 403응답")
    public void forbiddenRequest_returns403() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException exception = new AccessDeniedException(FORBIDDEN);


        StringWriter out = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(out));

        accessDeniedHandler.handle(request, response, exception);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertThat(out.toString()).contains(FORBIDDEN);
    }

}