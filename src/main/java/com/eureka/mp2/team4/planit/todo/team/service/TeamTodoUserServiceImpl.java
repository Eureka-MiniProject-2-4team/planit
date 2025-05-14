package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoUserDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.MyTeamListResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.MyTeamTodoDetailResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoWithUserStatusResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamWithTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 페이지 로드 시 호출
    @Override
    public ApiResponse getMyTeamListAndTodoList(String userId) {
        try {
            // 1. 사용자가 가입된 팀 리스트 조회
            List<UserTeamDto> userTeamDtoList = userTeamMapper.getMyTeamList(userId);
            if (userTeamDtoList == null || userTeamDtoList.isEmpty()) {
                return ApiResponse.<MyTeamListResponseDto>builder()
                        .result(Result.FAIL)
                        .message(NOT_FOUND_MY_TEAMLIST)
                        .build();
            }

            // 2. 최종 결과를 담을 리스트 생성
            List<TeamWithTodoResponseDto> teamsWithTodos = new ArrayList<>();

            // 3. 각 팀별로 투두 리스트 조회
            for (UserTeamDto userTeamDto : userTeamDtoList) {
                String teamId = userTeamDto.getTeamId();

                // 3-1. 팀 정보와 투두를 담을 DTO 생성
                TeamWithTodoResponseDto teamWithTodos = new TeamWithTodoResponseDto();
                teamWithTodos.setTeamId(teamId);

                // 3-2. 해당 팀의, 팀 투두 리스트 조회
                List<TeamTodoDto> teamTodoDtoList = teamTodoMapper.getTeamTodoList(teamId);
                List<TeamTodoWithUserStatusResponseDto> todosWithStatus = new ArrayList<>();

                // 3-3. 각 투두별로 사용자 완료 상태 조회 및 가공
                for (TeamTodoDto teamTodoDto : teamTodoDtoList) {
                    String todoId = teamTodoDto.getId();

                    // 3-4. 사용자의 투두 완료 상태 조회 - DB에서 가져옴
                    TeamTodoUserDto myTeamTodoDto = myMapper.getMyTeamTodoDetail(todoId, userId);
                    boolean isCompleted = false;

                    // DB에서 가져온 완료 상태 적용
                    if (myTeamTodoDto != null) {
                        isCompleted = myTeamTodoDto.isCompleted(); // DB에서 가져온 상태값
                    }

                    // 3-5. 투두와 상태 정보를 담는 DTO 생성
                    TeamTodoWithUserStatusResponseDto todoWithStatus = new TeamTodoWithUserStatusResponseDto();
                    todoWithStatus.setTodoId(todoId);
                    todoWithStatus.setTitle(teamTodoDto.getTitle());
                    todoWithStatus.setContent(teamTodoDto.getContent());
                    todoWithStatus.setUpdatedAt(myTeamTodoDto.getUpdatedAt());
                    todoWithStatus.setCompleted(isCompleted);

                    todosWithStatus.add(todoWithStatus);
                }

                // 3-6. 팀 DTO에 투두 리스트 설정
                teamWithTodos.setTodos(todosWithStatus);
                teamsWithTodos.add(teamWithTodos);
            }

            // 4. 최종 응답 생성
            MyTeamListResponseDto response = new MyTeamListResponseDto();
            response.setTeams(teamsWithTodos);

            return ApiResponse.<MyTeamListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message(GET_MY_TEAM_AND_TODO_LIST_SUCCESS)
                    .data(response)
                    .build();

        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return ApiResponse.<MyTeamListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(GET_MY_TEAM_AND_TODO_LIST_FAIL)
                    .build();
        }
    }

    // 팀 -> 팀 목록에서 팀1 선택 -> 팀1 의 투두 리스트에서 선택 -> 상세 조회
    // 캘린더 -> 투두 선택 -> 상세 조회
    @Override
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
    public ApiResponse updateMyTeamTodo(MyTeamTodoRequestDto myTeamTodoRequestDto) {
        try{
            if(teamTodoMapper.getTeamTodoById(myTeamTodoRequestDto.getTeamTodoId()) == null ) {
                throw new NotFoundException(NOT_FOUND_TEAMTODO_ID);
            }
            TeamTodoUserDto teamTodoUserDto = TeamTodoUserDto.builder()
                    .teamTodoId(myTeamTodoRequestDto.getTeamTodoId())
                    .userId(myTeamTodoRequestDto.getUserId())
                    .isCompleted(updateStatus(myTeamTodoRequestDto.isCompleted()))
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

    public boolean updateStatus(boolean status){
        return status? false : true;
    }
}
