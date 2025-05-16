package com.eureka.mp2.team4.planit.todo.team.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TeamTodoRequestDto {
    private String id;
    private String teamId;
    private String title;
    private String content;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
    private Timestamp createAt;
    private Timestamp updateAt;
    private LocalDateTime targetDate;
}
