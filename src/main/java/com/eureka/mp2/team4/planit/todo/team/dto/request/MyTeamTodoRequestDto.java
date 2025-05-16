package com.eureka.mp2.team4.planit.todo.team.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MyTeamTodoRequestDto {
    private String teamTodoId;
    private String userId;
    @JsonProperty("isCompleted")
    private boolean isCompleted;
}
