package com.eureka.mp2.team4.planit.auth.handler;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.BAD_LOGIN_REQUEST;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_CREDENTIALS;

public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ApiResponse<?> failResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message(exception.getMessage())
                .build();

        if (exception.getMessage().equals(BAD_LOGIN_REQUEST)) {
            ResponseUtils.writeJson(response, failResponse, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (exception.getMessage().equals(INVALID_CREDENTIALS)) {
            ResponseUtils.writeJson(response, failResponse, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ResponseUtils.writeJson(response, failResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
