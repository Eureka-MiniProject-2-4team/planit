package com.eureka.mp2.team4.planit.todo.team.mapper;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TeamTodoMapper {
    void createTeamTodo(TeamTodoDto teamTodoDto);

    // 팀 자체의 투두 리스트 가져오기
    List<TeamTodoDto> getTeamTodoList(String teamId);
    TeamTodoDto getTeamTodoById(String teamTodoId);
    Integer existTeamTodoByTeamId(String teamId);
    Integer countTeamTodoList(String teamId);
    List<TeamTodoDto> findTeamTodoByTargetDate(@Param("teamId") String teamId, @Param("targetDate") LocalDateTime targetDate);

    void updateTeamTodo(TeamTodoDto teamTodoDto);

    void deleteTeamTodo(String teamTodoId);
}
