package com.eureka.mp2.team4.planit.user.dto.response;

import com.eureka.mp2.team4.planit.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {
    private String id;
    private String email;
    private String userName;
    private String nickname;
    private int friendCount;
    private int teamCount;
    private int todoCount;
}
