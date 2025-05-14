package com.eureka.mp2.team4.planit.todo.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamTodoUserDto {
    private String teamTodoId;
    private String userId;
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
