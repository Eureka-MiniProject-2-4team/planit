package com.eureka.mp2.team4.planit.user.dto.response;

import com.eureka.mp2.team4.planit.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {
    private final String id;
    private final String email;
    private final String userName;
    private final String nickname;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean isActive;
    private final String phoneNumber;
}
