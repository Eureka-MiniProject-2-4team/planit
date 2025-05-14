package com.eureka.mp2.team4.planit.todo.team.mapper;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamTodoUserMapper {

    void registerTeamTodoToMembers(@Param("teamTodoId") String teamTodoId, @Param("userId") String userId);
    List<TeamTodoUserDto> getMyTeamTodoList(@Param("teamId") String teamId, @Param("userId") String userId);
    // 나의 팀 투두 완료 상태 리스트로 가져오기
    TeamTodoUserDto getMyTeamTodoDetail(@Param("teamTodoId") String teamTodoId, @Param("userId") String userId);

    void updateMyTeamTodo(TeamTodoUserDto teamTodoUserDto);
}
