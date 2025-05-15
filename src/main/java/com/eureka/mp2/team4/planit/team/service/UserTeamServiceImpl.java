package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.response.MyTeamResponseDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;
import static com.eureka.mp2.team4.planit.team.constants.Team_Role.MEMBER;
import static com.eureka.mp2.team4.planit.team.constants.Team_Status.WAIT;

@Service
@RequiredArgsConstructor
public class UserTeamServiceImpl implements UserTeamService {

    private final UserTeamMapper userTeamMapper;
    private final TeamMapper teamMapper;

    @Override
    public ApiResponse inviteTeamMember(UserTeamRequestDto userTeamRequestDto) {
        try {
            String userId = userTeamMapper.findUserIdByNickNameOREmail(userTeamRequestDto.getSearch());

            UserTeamDto userTeamDto = UserTeamDto.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .teamId(userTeamRequestDto.getTeamId())
                    .status(WAIT)
                    .role(MEMBER)
                    .build();

            userTeamMapper.registerTeamMember(userTeamDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(REGISTER_TEAM_MEMBER_SUCCESS)
                    .build();

        } catch (DuplicateKeyException de) {
            de.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(DUPLICATED_INVITED)
                    .build();
        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(REGISTER_TEAM_MEMBER_FAIL)
                    .build();
        }
    }

    // user 테이블 건드리는것 보류
    @Override
    public ApiResponse getTeamMemberList(String teamId) {
       try{
           List<UserTeamDto> userTeamDtoList = userTeamMapper.getTeamMemberList(teamId);

           return ApiResponse.builder()
                   .result(Result.SUCCESS)
                   .message(GET_TEAM_MEMBER_LIST_SUCCESS)
                   .data(userTeamDtoList)
                   .build();

       } catch (Exception e){
           e.printStackTrace();
           return ApiResponse.builder()
                   .result(Result.FAIL)
                   .message(GET_TEAM_MEMBER_LIST_FAIL)
                   .build();
       }
    }

    @Override
    public ApiResponse getMyTeamList(String userId) {
        try {
            if (userTeamMapper.getMyTeamList(userId) == null) {
                throw new NotFoundException(NOT_FOUND_MY_TEAMLIST);
            }
            List<UserTeamDto> userTeamDtoList = userTeamMapper.getMyTeamList(userId);
            List<MyTeamResponseDto> teamList = new ArrayList<>();

            for (UserTeamDto userTeamDto : userTeamDtoList) {
                TeamDto teamDto = teamMapper.findTeamById(userTeamDto.getTeamId());

                MyTeamResponseDto myTeamResponseDto = new MyTeamResponseDto(teamDto, userTeamDto);
                teamList.add(myTeamResponseDto);
            }

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_MY_TEAMLIST_SUCCESS)
                    .data(teamList)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_MY_TEAMLIST_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse getMyInvitedList(String userId) {
        try {
            if (userTeamMapper.getMyInvitedList(userId) == null) {
                throw new NotFoundException(NOT_FOUND_MY_INVITED);
            }
            List<UserTeamDto> userTeamDtoList = userTeamMapper.getMyInvitedList(userId);
            List<MyTeamResponseDto> teamList = new ArrayList<>();

            for (UserTeamDto userTeamDto : userTeamDtoList) {
                TeamDto teamDto = teamMapper.findTeamById(userTeamDto.getTeamId());

                MyTeamResponseDto myTeamResponseDto = new MyTeamResponseDto(teamDto, userTeamDto);
                teamList.add(myTeamResponseDto);
            }

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(GET_MY_INVITED_SUCCESS)
                    .data(teamList)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(GET_MY_INVITED_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse acceptTeamJoin(String teamId, String userId) {
        try {
            if (teamMapper.findTeamById(teamId) == null) {
                throw new NotFoundException(NOT_FOUND_ID);
            }

            userTeamMapper.acceptTeamJoin(teamId, userId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(ACCEPT_TEAM_JOIN_SUCCESS)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(ACCEPT_TEAM_JOIN_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse deleteTeamMember(String teamId, String userId) {
        try {
            if (userTeamMapper.findByTeamIdAndUserId(teamId, userId) == null) {
                throw new NotFoundException(NOT_FOUND_USER_ID);
            }

            userTeamMapper.deleteTeamMember(teamId, userId);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(DELETE_TEAM_MEMBER_SUCCESS)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(DELETE_TEAM_MEMBER_FAIL)
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse denyTeamMember(String teamId, String userId) {
        try {
            if (teamMapper.findTeamById(teamId) == null) {
                throw new NotFoundException(NOT_FOUND_ID);
            }

            if(userTeamMapper.isTeamLeader(teamId, userId) == 1) {
                throw new BadRequestException(LEADER_CANNOT_DENY_TEAM);
            } else {
                userTeamMapper.deleteTeamMember(teamId, userId);

                return ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(QUIT_TEAM_MEMBER_SUCCESS)
                        .build();
            }
        }catch(BadRequestException be) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(LEADER_CANNOT_DENY_TEAM)
                    .build();
        }
        catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(QUIT_TEAM_MEMBER_FAIL)
                    .build();
        }
    }

}
