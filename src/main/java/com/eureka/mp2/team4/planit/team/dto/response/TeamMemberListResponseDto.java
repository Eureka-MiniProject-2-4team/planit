package com.eureka.mp2.team4.planit.team.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public class TeamMemberListResponseDto {
    private String userId;
    private String userName;
    private String userEmail;
    private String userNickName;
    private String role;
    private Timestamp joinedAt;
}
