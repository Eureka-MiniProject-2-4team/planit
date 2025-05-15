package com.eureka.mp2.team4.planit.team.dto.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserTeamRequestDto {
    private String userId;
    private String teamId;
    private Timestamp joinedAt;
    private String status;

    private String search;
}
