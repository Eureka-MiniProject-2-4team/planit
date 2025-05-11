package com.eureka.mp2.team4.planit.auth.security;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class PlanitUserDetailsTest {

    @Test
    @DisplayName("UserDetails 정보가 정확히 매핑되는지 확인")
    void userDetailsMapping_success() {
        UserDto userDto = new UserDto(
                "uuid-123",
                "user@example.com",
                "username",
                "hashedPassword",
                "nickname",
                UserRole.ROLE_USER,
                null, null, true,
                "01012345678"
        );

        PlanitUserDetails userDetails = new PlanitUserDetails(userDto);

        assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");

        Collection<?> authorities = userDetails.getAuthorities();
        assertThat(authorities).hasSize(1);
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_USER");
    }
}
