package com.eureka.mp2.team4.planit.todo.team.dto.response;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import lombok.Data;

import java.util.List;

@Data
public class TeamTodoListResponseDto {
    List<TeamTodoDto> teamTodoDtoList;
}
