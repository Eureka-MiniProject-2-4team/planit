package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;

public interface TeamTodoService {
    ApiResponse createTeamTodo(TeamTodoRequestDto teamTodoRequestDto);

    ApiResponse getTeamTodoList(String teamId);
    ApiResponse getTeamTodoById(String teamTodoId);

    ApiResponse updateTeamTodo(TeamTodoRequestDto teamTodoRequestDto);

    ApiResponse deleteTeamTodo(String teamTodoId);
}
