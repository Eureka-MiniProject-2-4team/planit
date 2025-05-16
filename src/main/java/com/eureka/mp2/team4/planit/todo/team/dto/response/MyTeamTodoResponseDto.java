package com.eureka.mp2.team4.planit.todo.team.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Builder
public class MyTeamTodoResponseDto {
    private String teamTodoId;
    private String title;
    private String content;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private LocalDateTime targetDate;
}
