package com.eureka.mp2.team4.planit.team.mapper;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.dto.response.UserTeamResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 팀원 관리
// 내가 속한 팀 목록 조회, 팀원 모두 조회, 팀 가입 초대, 팀원 강퇴
@Mapper
public interface UserTeamMapper {
    // CREATE
    void registerTeamMember(UserTeamDto userTeamDto); // 팀에 멤버를 등록 ( 팀장 포함 ), 서비스에서 팀장과 팀원 구분

    // READ
    List<UserTeamDto> getTeamMemberList(String teamId);

    List<UserTeamDto> getMyTeamList(String userId);

    List<UserTeamDto> getMyInvitedList(String userId);

    UserTeamDto findByTeamIdAndUserId(@Param("teamId") String teamId, @Param("userId") String userId);

    Integer isTeamMember(@Param("teamId") String teamId, @Param("userId") String userId);

    Integer isTeamLeader(@Param("teamId") String teamId, @Param("userId") String userId);

    // UPDATE
    void acceptTeamJoin(@Param("teamId") String teamId, @Param("userId") String userId);

    // DELETE
    void deleteTeamMember(@Param("teamId") String teamId, @Param("userId") String userId); // 팀장이 타인 강퇴, 팀원이 자진 탈퇴 및 가입 거절 -> 같은 쿼리 사용

    List<TeamDto> findTeamByLeaderId(@Param("userId") String userId);    // 팀장아이디로 된 검색
}
