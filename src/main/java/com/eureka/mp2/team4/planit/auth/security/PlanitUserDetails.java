package com.eureka.mp2.team4.planit.auth.security;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PlanitUserDetails implements UserDetails {
    private final UserDto userDto;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> userDto.getRole().name());
    }

    @Override
    public String getPassword() {
        return userDto.getPassword();
    }

    @Override
    public String getUsername() {   // 이메일이 로그인 id
        return userDto.getEmail();
    }
}
