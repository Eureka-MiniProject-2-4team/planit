package com.eureka.mp2.team4.planit.todo.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamTodoUserDto {
    private String teamTodoId;
    private String userId;
    private String title;
    private String content;
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private LocalDateTime targetDate;

}
