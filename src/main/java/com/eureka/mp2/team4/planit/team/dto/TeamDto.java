package com.eureka.mp2.team4.planit.team.dto;

import lombok.*;

import java.sql.Timestamp;

// UUID 중복 체크 로직 필요
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {
    private String id;
    private String teamName;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
