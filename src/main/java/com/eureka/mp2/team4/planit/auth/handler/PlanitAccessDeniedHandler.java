package com.eureka.mp2.team4.planit.auth.handler;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.ACCESS_DENIED;

@Component
public class PlanitAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .result(Result.FORBIDDEN)
                .message(ACCESS_DENIED)
                .build();

        ResponseUtils.writeJson(response, apiResponse, HttpServletResponse.SC_FORBIDDEN);
    }
}
