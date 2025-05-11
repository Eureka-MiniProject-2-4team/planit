package com.eureka.mp2.team4.planit.user.dto;

import com.eureka.mp2.team4.planit.user.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
public class UserDto {
    private final String id;
    private final String email;
    private final String username;
    private final String password;
    private final String nickname;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isActive;
    private final String phone;
}
