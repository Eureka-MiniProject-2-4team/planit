package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyTeamTodoDetailResponseDto {
    private String title;
    private String content;
    private boolean isCompleted;
    private Timestamp updatedAt;
}
