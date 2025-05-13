package com.eureka.mp2.team4.planit.team;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockPlanitUserFactory implements WithSecurityContextFactory<WithMockPlanitUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockPlanitUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // UserDto 생성
        UserDto userDto = new UserDto(
                annotation.username(),  // ID로 사용
                "test@example.com",
                annotation.username(),  // 사용자명도 동일하게
                "password123",
                "Test User",
                UserRole.valueOf(annotation.role()),
                null,
                null,
                true,
                "01000001112"
        );

        // PlanitUserDetails 생성
        PlanitUserDetails userDetails = new PlanitUserDetails(userDto);

        // Authentication 객체 생성 및 SecurityContext에 설정
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,  // Principal로 PlanitUserDetails 사용
                        null,
                        userDetails.getAuthorities());

        context.setAuthentication(auth);
        return context;
    }
}
