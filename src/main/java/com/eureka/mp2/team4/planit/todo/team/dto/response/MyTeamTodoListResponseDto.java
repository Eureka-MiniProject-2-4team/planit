package com.eureka.mp2.team4.planit.todo.team.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTeamTodoListResponseDto {
    List<MyTeamTodoResponseDto> myTeamTodoStatus;
}
