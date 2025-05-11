package com.eureka.mp2.team4.planit.auth.handler;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.NEED_LOGIN;

@Component
public class PlanitAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .result(Result.NEED_LOGIN)
                .message(NEED_LOGIN)
                .build();
        ResponseUtils.writeJson(response, apiResponse, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
