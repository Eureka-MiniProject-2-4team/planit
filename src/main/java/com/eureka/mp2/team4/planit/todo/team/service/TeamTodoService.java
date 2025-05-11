package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoReqDto;

public interface TeamTodoService {
    ApiResponse createTeamTodo(TeamTodoReqDto teamTodoReqDto);

    ApiResponse getTeamTodoList(String teamId);
    ApiResponse getTeamTodoById(String teamTodoId);

    ApiResponse updateTeamTodo(TeamTodoReqDto teamTodoReqDto);

    ApiResponse deleteTeamTodo(String teamTodoId);
}
