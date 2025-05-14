package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MyTeamListResponseDto {
    private List<TeamWithTodoResponseDto> teams;
}
