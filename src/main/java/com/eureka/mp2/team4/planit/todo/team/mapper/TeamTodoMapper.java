package com.eureka.mp2.team4.planit.todo.team.mapper;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamTodoMapper {
    void createTeamTodo(TeamTodoDto teamTodoDto);

    List<TeamTodoDto> getTeamTodoList(String teamId);
    TeamTodoDto getTeamTodoById(String teamTodoId);

    void updateTeamTodo(TeamTodoDto teamTodoDto);

    void deleteTeamTodo(String teamTodoId);
}
