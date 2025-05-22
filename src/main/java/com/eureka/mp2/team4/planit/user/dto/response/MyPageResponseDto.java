package com.eureka.mp2.team4.planit.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponseDto {
    private String id;
    private String email;
    private String userName;
    private String nickname;
    private int friendCount;
    private int teamCount;
    private int todoCount;
}
