package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoUserDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.*;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.NOT_FOUND_MY_TEAMLIST;
import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;

@Service
@RequiredArgsConstructor
public class TeamTodoUserServiceImpl implements TeamTodoUserService{

    private final TeamTodoUserMapper myMapper;
    private final TeamTodoMapper teamTodoMapper;
    private final UserTeamMapper userTeamMapper;
    private final TeamTodoUserMapper teamTodoUserMapper;

    // 페이지 로드 시 호출
    @Override
    @Transactional
    public ApiResponse getMyTeamListAndTodoList(String teamId, String userId) {
        try {
            if(teamTodoMapper.existTeamTodoByTeamId(teamId) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_DATA);
            }
            if(userTeamMapper.getMyTeamList(userId) == null){
                throw new NotFoundException(NOT_FOUND_MY_TEAMLIST);
            }

            List<TeamTodoUserDto> list = teamTodoUserMapper.getMyTeamTodoList(teamId, userId);
            List<MyTeamTodoResponseDto> responseDtoList = new ArrayList<>();

            for(TeamTodoUserDto user : list){
                MyTeamTodoResponseDto responseDto = MyTeamTodoResponseDto.builder()
                        .teamTodoId(user.getTeamTodoId())
                        .title(user.getTitle())
                        .content(user.getContent())
                        .isCompleted(user.isCompleted())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build();

                responseDtoList.add(responseDto);
            }

            MyTeamTodoListResponseDto finalResponseDtoList = MyTeamTodoListResponseDto.builder()
                    .myTeamTodoStatus(responseDtoList)
                    .build();

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_MY_TEAM_AND_TODO_LIST_SUCCESS)
                    .data(finalResponseDtoList)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_MY_TEAM_AND_TODO_LIST_FAIL)
                    .build();
        }
    }

    // 팀 -> 팀 목록에서 팀1 선택 -> 팀1 의 투두 리스트에서 선택 -> 상세 조회
    // 캘린더 -> 투두 선택 -> 상세 조회
    @Override
    @Transactional
    public ApiResponse getMyTeamTodoDetail(String teamId, String teamTodoId, String userId) {
        try{
            if(teamTodoMapper.getTeamTodoById(teamTodoId) == null){
                throw new NotFoundException(NOT_FOUND_TEAMTODO_ID);
            }

            TeamTodoDto teamTodoDto = teamTodoMapper.getTeamTodoById(teamTodoId);
            TeamTodoUserDto teamTodoUserDto = myMapper.getMyTeamTodoDetail(teamTodoId, userId);

            MyTeamTodoDetailResponseDto myTeamTodoDetailResponseDto = MyTeamTodoDetailResponseDto.builder()
                    .title(teamTodoDto.getTitle())
                    .content(teamTodoDto.getContent())
                    .isCompleted(teamTodoUserDto.isCompleted())
                    .updatedAt(teamTodoUserDto.getUpdatedAt())
                    .build();

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_MY_TEAM_TODO_SUCCESS)
                    .data(myTeamTodoDetailResponseDto)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_MY_TEAM_TODO_FAIL)
                    .build();
        }
    }

    // 이미 1이면 0으로, 아니라면 1로
    @Override
    @Transactional
    public ApiResponse updateMyTeamTodo(MyTeamTodoRequestDto myTeamTodoRequestDto) {
        try{
            if(teamTodoMapper.getTeamTodoById(myTeamTodoRequestDto.getTeamTodoId()) == null ) {
                throw new NotFoundException(NOT_FOUND_TEAMTODO_ID);
            }
            TeamTodoUserDto teamTodoUserDto = TeamTodoUserDto.builder()
                    .teamTodoId(myTeamTodoRequestDto.getTeamTodoId())
                    .userId(myTeamTodoRequestDto.getUserId())
                    .isCompleted(!(myTeamTodoRequestDto.isCompleted()))
                    .build();

            myMapper.updateMyTeamTodo(teamTodoUserDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_MY_TEAMTODO_SUCCESS)
                    .build();

        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(UPDATE_MY_TEAMTODO_FAIL)
                    .build();
        }
    }

}
