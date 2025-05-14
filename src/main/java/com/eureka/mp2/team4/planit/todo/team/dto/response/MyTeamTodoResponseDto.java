package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public class MyTeamTodoResponseDto {
    private String teamTodoId;
    private String teamId;
    private boolean isCompleted;
    private Timestamp updatedAt;
}
