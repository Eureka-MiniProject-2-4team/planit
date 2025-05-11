package com.eureka.mp2.team4.planit.auth.security;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_CREDENTIALS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlanitUserDetailServiceTest {

    private final UserMapper userMapper = mock(UserMapper.class);
    private final PlanitUserDetailService service = new PlanitUserDetailService(userMapper);

    @Test
    @DisplayName("[단위] 존재하는 유저 이메일로 UserDetails 반환")
    void loadUserByUsername_returnsUserDetails() {
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

        when(userMapper.findByEmail("user@example.com")).thenReturn(userDto);

        UserDetails result = service.loadUserByUsername("user@example.com");

        assertThat(result).isInstanceOf(PlanitUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("[단위] 존재하지 않는 이메일 - 예외 발생")
    void loadUserByUsername_userNotFound_throws() {
        when(userMapper.findByEmail("notfound@example.com")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("notfound@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(INVALID_CREDENTIALS);
    }
}
