package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final UserTeamMapper userTeamMapper;

    @Override
    public ApiResponse registerTeam(String userId, TeamRequestDto teamRequestDto) {
        try {
            TeamDto teamDto = TeamDto.builder()
                    .id(UUID.randomUUID().toString())
                    .teamName(teamRequestDto.getTeamName())
                    .description(teamRequestDto.getDescription())
                    .build();

            UserTeamDto userTeamDto = UserTeamDto.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .teamId(teamDto.getId())
                    .status("가입")
                    .role("팀장")
                    .build();

            // 팀 등록
            teamMapper.registerTeam(teamDto);
            userTeamMapper.registerTeamMember(userTeamDto);

            // 성공 응답
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(REGISTER_TEAM_SUCCESS)
                    .build();

        } catch (Exception e) {
            // 실패 응답
            System.out.println(e.getMessage());
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(REGISTER_TEAM_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse getTeamById(String teamId) {
        try {
            if (teamMapper.findTeamById(teamId) == null) {
                throw new NotFoundException(NOT_FOUND_ID);
            }

            TeamDto teamDto = teamMapper.findTeamById(teamId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_TEAM_SUCCESS)
                    .data(teamDto)
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_TEAM_FAIL)
                    .build();
        }
    }


    @Override
    public ApiResponse updateTeam(TeamRequestDto teamRequestDto) {
        try {

            if (teamMapper.findTeamById(teamRequestDto.getId()) == null) {
                throw new NotFoundException(NOT_FOUND_ID);
            }

            TeamDto teamDto = TeamDto.builder()
                    .id(teamRequestDto.getId())
                    .teamName(teamRequestDto.getTeamName())
                    .description(teamRequestDto.getDescription())
                    .build();

            teamMapper.updateTeam(teamDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_TEAM_SUCCESS)
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(UPDATE_TEAM_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse deleteTeam(String teamId) {
        try {
            if (teamMapper.findTeamById(teamId) == null) {
                throw new NotFoundException(NOT_FOUND_ID);
            }

            teamMapper.deleteTeam(teamId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(DELETE_TEAM_SUCCESS)
                    .build();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(DELETE_TEAM_FAIL)
                    .build();
        }
    }

    @Override
    public boolean isTeamMember(String userId, String teamId) {
        // todo : userId가 teamId의 멤버인지 체크
        return true;
    }

    @Override
    public boolean isTeamLeader(String userId, String teamId) {
       // todo : userId가 teamId의 팀 리터인지 체크
        return false;
    }
}
