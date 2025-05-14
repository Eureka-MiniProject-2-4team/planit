package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;

public interface TeamTodoUserService {
    ApiResponse getMyTeamListAndTodoList (String teamId, String userId);
    ApiResponse getMyTeamTodoDetail (String teamId, String teamTodoUserId, String userId);

    ApiResponse updateMyTeamTodo (MyTeamTodoRequestDto myTeamTodoRequestDto);
}
