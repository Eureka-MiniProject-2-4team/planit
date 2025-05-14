package com.eureka.mp2.team4.planit.team.dto.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserTeamResponseDto {
    private String userId;
    private Timestamp joinedAt;
    private String role;
}
