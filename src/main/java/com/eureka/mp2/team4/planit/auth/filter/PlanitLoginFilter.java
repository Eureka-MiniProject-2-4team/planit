package com.eureka.mp2.team4.planit.auth.filter;

import com.eureka.mp2.team4.planit.auth.dto.request.UserLoginDto;
import com.eureka.mp2.team4.planit.auth.dto.response.LoginResponseDto;
import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.JwtService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.BAD_LOGIN_REQUEST;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.LOGIN_SUCCESS;

@RequiredArgsConstructor
public class PlanitLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserLoginDto loginDto = new ObjectMapper().readValue(request.getInputStream(), UserLoginDto.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException(BAD_LOGIN_REQUEST);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        PlanitUserDetails userDetails = (PlanitUserDetails) authResult.getPrincipal();
        String token = jwtService.createToken(userDetails.getUserDto());
        LoginResponseDto loginResponseDto = new LoginResponseDto(token);
        ApiResponse<?> successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(LOGIN_SUCCESS)
                .data(loginResponseDto)
                .build();
        ResponseUtils.writeJson(response, successResponse, HttpServletResponse.SC_OK);
    }
}
