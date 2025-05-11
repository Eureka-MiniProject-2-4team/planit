package com.eureka.mp2.team4.planit.common.config;

import com.eureka.mp2.team4.planit.auth.filter.JwtAuthenticationFilter;
import com.eureka.mp2.team4.planit.auth.filter.PlanitLoginFilter;
import com.eureka.mp2.team4.planit.auth.handler.LoginFailureHandler;
import com.eureka.mp2.team4.planit.auth.handler.PlanitAccessDeniedHandler;
import com.eureka.mp2.team4.planit.auth.handler.PlanitAuthenticationEntryPoint;
import com.eureka.mp2.team4.planit.auth.service.JwtService;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 아래 설정으로 보안 기능 전체 비활성화
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception {
        AuthenticationManager authenticationManager = authConfig.getAuthenticationManager();

        PlanitLoginFilter loginFilter = new PlanitLoginFilter(authenticationManager, jwtService);
        loginFilter.setFilterProcessesUrl("/auth/login");
        loginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/**").hasRole("USER")
                        .anyRequest().permitAll())   // 임시로 모두 허용
                .exceptionHandling(excetion -> excetion
                        .accessDeniedHandler(new PlanitAccessDeniedHandler())
                        .authenticationEntryPoint(new PlanitAuthenticationEntryPoint()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
