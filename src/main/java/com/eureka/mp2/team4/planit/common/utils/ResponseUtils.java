package com.eureka.mp2.team4.planit.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {
    public static void writeJson(HttpServletResponse response, Object body, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
