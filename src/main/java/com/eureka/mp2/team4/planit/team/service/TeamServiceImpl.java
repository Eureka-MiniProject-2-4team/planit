package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.response.TeamResponseDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService{

    private TeamMapper teamMapper;

    @Override
    public ApiResponse registerTeam(TeamRequestDto teamRequestDto) {
        try {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            TeamDto teamDto = TeamDto.builder()
                    .id(UUID.randomUUID())
                    .teamName(teamRequestDto.getTeamName())
                    .description(teamRequestDto.getDescription())
                    .createdAt(currentTime)
                    .updatedAt(currentTime)
                    .build();

            // 팀 등록
            teamMapper.registerTeam(teamDto);

            // 성공 응답
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("팀이 성공적으로 등록되었습니다.")
                    .build();

        } catch (Exception e) {
            // 실패 응답
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message("팀 등록에 실패했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse updateTeam(TeamRequestDto teamRequestDto) {
        try{
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());

            TeamDto teamDto = TeamDto.builder()
                    .id(teamRequestDto.getId())
                    .teamName(teamRequestDto.getTeamName())
                    .description(teamRequestDto.getDescription())
                    .updatedAt(currentTime)
                    .build();

            teamMapper.updateTeam(teamDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("팀이 성공적으로 수정되었습니다.")
                    .build();

        } catch (Exception e){
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message("팀 수정에 실패했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse deleteTeam(UUID teamId) {
        try{
            teamMapper.deleteTeam(teamId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("팀이 성공적으로 삭제되었습니다.")
                    .build();

        } catch(Exception e){
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message("팀 삭제가 실패했습니다: " + e.getMessage())
                    .build();
        }
    }
}
