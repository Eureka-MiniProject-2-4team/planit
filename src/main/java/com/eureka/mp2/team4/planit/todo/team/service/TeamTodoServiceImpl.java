package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;
@Service
@RequiredArgsConstructor
public class TeamTodoServiceImpl implements TeamTodoService {

    private final TeamTodoMapper teamTodoMapper;

    @Override
    public ApiResponse createTeamTodo(TeamTodoRequestDto teamTodoRequestDto) {
        try{
            TeamTodoDto teamTodoDto = TeamTodoDto.builder()
                    .id(UUID.randomUUID().toString())
                    .teamId(teamTodoRequestDto.getTeamId())
                    .title(teamTodoRequestDto.getTitle())
                    .content(teamTodoRequestDto.getContent())
                    .isCompleted(false)
                    .build();

            teamTodoMapper.createTeamTodo(teamTodoDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(REGISTER_TEAMTODO_SUCCESS)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(REGISTER_TEAMTODO_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse getTeamTodoList(String teamId) {
        try{
            if(teamTodoMapper.getTeamTodoList(teamId) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_DATA);
            }

            List<TeamTodoDto> teamTodoDtoList = teamTodoMapper.getTeamTodoList(teamId);

            // TODO : TeamTodoResDto 로 다시 감싸서 id, teamId 값 빼고 줘야 맞나?
            TeamTodoListResponseDto response = new TeamTodoListResponseDto();
            response.setTeamTodoDtoList(teamTodoDtoList);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_TEAMTODO_LIST_SUCCESS)
                    .data(response)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_TEAMTODO_LIST_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse getTeamTodoById(String teamTodoId) {
        try{
            if(teamTodoMapper.getTeamTodoById(teamTodoId) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_DATA);
            }

            TeamTodoDto teamTodoDto = teamTodoMapper.getTeamTodoById(teamTodoId);
            TeamTodoResponseDto response = TeamTodoResponseDto.builder()
                    .title(teamTodoDto.getTitle())
                    .content(teamTodoDto.getContent())
                    .isCompleted(teamTodoDto.isCompleted())
                    .createdAt(teamTodoDto.getCreatedAt())
                    .updatedAt(teamTodoDto.getUpdatedAt())
                    .build();

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_TEAMTODO_SUCCESS)
                    .data(response)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_TEAMTODO_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse updateTeamTodo(TeamTodoRequestDto teamTodoRequestDto) {
        try{
            if(teamTodoMapper.getTeamTodoById(teamTodoRequestDto.getId()) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_ID);
            }

            TeamTodoDto teamTodoDto = TeamTodoDto.builder()
                    .id(teamTodoRequestDto.getId())
//                    .teamId(teamTodoReqDto.getTeamId()) // 필요없을듯
                    .title(teamTodoRequestDto.getTitle())
                    .content(teamTodoRequestDto.getContent())
                    .isCompleted(teamTodoRequestDto.isCompleted())
                    .build();

            teamTodoMapper.updateTeamTodo(teamTodoDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_TEAMTODO_SUCCESS)
                    .build();

        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(UPDATE_TEAMTODO_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse deleteTeamTodo(String teamTodoId) {
        try{
            if(teamTodoMapper.getTeamTodoById(teamTodoId) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_ID);
            }

            teamTodoMapper.deleteTeamTodo(teamTodoId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(DELETE_TEAMTODO_SUCCESS)
                    .build();

        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(DELETE_TEAMTODO_FAIL)
                    .build();
        }
    }
}
