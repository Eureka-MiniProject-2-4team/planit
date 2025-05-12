package com.eureka.mp2.team4.planit.team.dto;

import lombok.*;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTeamDto {
    private String id;
    private String userId;
    private String teamId;
    private Timestamp joinedAt;
    private String status;
    private String role;
}
