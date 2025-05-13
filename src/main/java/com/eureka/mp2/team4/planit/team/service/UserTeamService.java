package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;

public interface UserTeamService {
    // 팀장
    // CREATE
    ApiResponse inviteTeamMember(UserTeamRequestDto userTeamRequestDto);

    // 팀원
    // READ
    ApiResponse getTeamMember(String userId); // user 테이블 건드려야함
    ApiResponse getTeamMemberList(String teamId); // user 테이블 건드려야함
    ApiResponse getMyTeamList(String userId);
    ApiResponse getMyInvitedList(String userId);

    // PUT
    ApiResponse acceptTeamJoin(String teamId, String userId);

    // DELETE
    ApiResponse deleteTeamMember(String teamId, String userId);
    ApiResponse denyTeamMember(String teamId, String userId);

}
