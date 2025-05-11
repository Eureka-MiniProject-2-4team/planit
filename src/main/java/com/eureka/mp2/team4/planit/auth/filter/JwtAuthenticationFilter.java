package com.eureka.mp2.team4.planit.auth.filter;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.JwtService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.utils.ResponseUtils;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.eureka.mp2.team4.planit.auth.constants.Constraints.AUTHORIZATION_HEADER;
import static com.eureka.mp2.team4.planit.auth.constants.Constraints.BEARER_PREFIX;
import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_TOKEN;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                String token = extractTokenFromRequestHeader(response, authorizationHeader);
                if (token == null) return;
                handleToken(request, token);
            }
        } catch (Exception e) {
            setUnauthorizedResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequestHeader(HttpServletResponse response, String authorizationHeader) throws IOException {
        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!jwtService.validateToken(token)) {
            setUnauthorizedResponse(response);
            return null;
        }
        return token;
    }

    private void handleToken(HttpServletRequest request, String token) {
        String userId = jwtService.getUserId(token);
        UserDto userDto = userMapper.findById(userId);
        if (userDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            PlanitUserDetails userDetails = new PlanitUserDetails(userDto);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }


    private void setUnauthorizedResponse(HttpServletResponse response) throws IOException {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .result(Result.UNAUTHORIZED)
                .message(INVALID_TOKEN)
                .build();
        ResponseUtils.writeJson(response, apiResponse, HttpServletResponse.SC_UNAUTHORIZED);
    }
}
