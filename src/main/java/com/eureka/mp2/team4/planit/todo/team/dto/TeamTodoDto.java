package com.eureka.mp2.team4.planit.todo.team.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamTodoDto {
    private String id;
    private String teamId;
    private String title;
    private String content;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private LocalDateTime targetDate;
}
