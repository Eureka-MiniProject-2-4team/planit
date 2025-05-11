package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamTodoResDto {
    private String title;
    private String content;
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
