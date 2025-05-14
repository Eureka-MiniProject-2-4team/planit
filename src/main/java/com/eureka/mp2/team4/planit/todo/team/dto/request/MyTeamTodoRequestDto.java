package com.eureka.mp2.team4.planit.todo.team.dto.request;

import lombok.Data;

@Data
public class MyTeamTodoRequestDto {
    private String teamTodoId;
    private String userId;
    private boolean isCompleted;
}
