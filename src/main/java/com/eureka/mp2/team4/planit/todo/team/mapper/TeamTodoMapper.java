package com.eureka.mp2.team4.planit.todo.team.mapper;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamTodoMapper {
    void createTeamTodo(TeamTodoDto teamTodoDto);

    // 팀 자체의 투두 리스트 가져오기
    List<TeamTodoDto> getTeamTodoList(String teamId);
    TeamTodoDto getTeamTodoById(String teamTodoId);
    Integer existTeamTodoByTeamId(String teamId);

    void updateTeamTodo(TeamTodoDto teamTodoDto);

    void deleteTeamTodo(String teamTodoId);
}
