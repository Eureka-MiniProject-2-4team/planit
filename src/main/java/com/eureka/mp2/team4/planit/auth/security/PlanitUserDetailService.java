package com.eureka.mp2.team4.planit.auth.security;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.INVALID_CREDENTIALS;

@Service
@RequiredArgsConstructor
public class PlanitUserDetailService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDto userDto = userMapper.findByEmail(email);
        if (userDto == null)
            throw new UsernameNotFoundException(INVALID_CREDENTIALS);
        return new PlanitUserDetails(userDto);
    }
}
