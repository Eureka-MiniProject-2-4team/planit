package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TeamTodoWithUserStatusResponseDto {
    private String todoId;
    private String title;
    private String content;
    private boolean isCompleted;
    private Timestamp updatedAt;
}
