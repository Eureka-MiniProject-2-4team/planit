package com.eureka.mp2.team4.planit.team.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.annotations.TeamCheck;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.service.UserTeamService;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@Tag(name="팀과 팀원 관리 CRUD API", description = "팀 자체의 CRUD 기능, *팀원 관리 API 는 별도로 존재 + 아직 유저 권한을 갖고있는지 체크하는 과정 미적용")
public class TeamController {
    private final TeamService teamService;
    private final UserTeamService userTeamService;

    @Operation(summary = "팀 새로 등록")
    @PostMapping("")
    public ResponseEntity<ApiResponse> registerTeam(@AuthenticationPrincipal PlanitUserDetails planitUserDetails,
                                                    @RequestBody TeamRequestDto teamRequestDto) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(teamService.registerTeam(userId, teamRequestDto));
    }

    // 팀장
    @Operation(summary = "팀원 초대", description = "팀장만 가능, 권한 미적용")
    @TeamCheck(onlyLeader = true)
    @PostMapping("/{teamId}/member/{userId}")
    public ResponseEntity<ApiResponse> inviteMember(@PathVariable("teamId") String teamId,
                                                    @RequestBody UserTeamRequestDto userTeamRequestDto,
                                                    @PathVariable("userId") String userId) {
        userTeamRequestDto.setTeamId(teamId);
        userTeamRequestDto.setUserId(userId);
        return ResponseEntity.ok(userTeamService.inviteTeamMember(userTeamRequestDto));
    }

    @Operation(summary = "팀 상세 조회", description = "특정 팀의 모든 정보를 PathVariable : id 를 통해 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    // 팀원 ( 팀장도 팀원이자 팀장이라서 가능 )
    @Operation(summary = "팀원 상세 조회", description = "특정 팀원(타인) 상세 조회")
    @TeamCheck
    @GetMapping("/{teamId}/member/{userId}")
    public ResponseEntity<ApiResponse> getTeamMember(@PathVariable("teamId") String teamId,
                                                     @PathVariable("userId") String userId) {
        return ResponseEntity.ok(userTeamService.getTeamMember(userId));
    }

    @Operation(summary = "팀원 전체 조회", description = "팀의 모든 구성원 리스트 조회")
    @TeamCheck
    @GetMapping("/{teamId}/member/list")
    public ResponseEntity<ApiResponse> getTeamMemberList(@PathVariable String teamId) {
        return ResponseEntity.ok(userTeamService.getTeamMemberList(teamId));
    }

    @Operation(summary = "나의 팀 리스트 조회", description = "내가 속한 팀 리스트 모두 조회")
    @GetMapping("/myteamlist")
    public ResponseEntity<ApiResponse> getMyTeamList(@AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(userTeamService.getMyTeamList(userId));
    }

    @Operation(summary = "초대받은 리스트 조회", description = "내가 초대받은 팀 리스트 모두 조회")
    @GetMapping("/invitelist")
    public ResponseEntity<ApiResponse> getMyInviteList(@AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(userTeamService.getMyInvitedList(userId));
    }

    @Operation(summary = "팀 정보 수정", description = "팀장만 가능한 수정작업, 아직 팀장 권한 확인 미적용")
    @TeamCheck(onlyLeader = true)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTeam(@PathVariable("id") String id, @RequestBody TeamRequestDto teamRequestDto) {
        teamRequestDto.setId(id);
        return ResponseEntity.ok(teamService.updateTeam(teamRequestDto));
    }

    @Operation(summary = "팀 가입 수락")
    @PutMapping("/{teamId}/member")
    public ResponseEntity<ApiResponse> acceptTeamJoin(@PathVariable String teamId,
                                                      @AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(userTeamService.acceptTeamJoin(teamId, userId));
    }

    // 팀장
    @Operation(summary = "팀 삭제", description = "팀장만 가능한 삭제작업, 아직 팀장 권한 확인 미적용")
    @TeamCheck(onlyLeader = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.deleteTeam(id));
    }

    @Operation(summary = "팀원 강퇴", description = "팀장만 가능, 권한 미적용")
    @TeamCheck(onlyLeader = true)
    @DeleteMapping("/{teamId}/member/{userId}")
    public ResponseEntity<ApiResponse> deleteMember(@PathVariable("teamId") String teamId,
                                                    @PathVariable("userId") String userId) {
        return ResponseEntity.ok(userTeamService.deleteTeamMember(teamId, userId));
    }

    @Operation(summary = "팀 가입 거절 및 탈퇴", description = "중간테이블에서 삭제")
    @DeleteMapping("/{teamId}/member")
    public ResponseEntity<ApiResponse> denyTeamJoin(@PathVariable String teamId,
                                                    @AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(userTeamService.denyTeamMember(teamId, userId));
    }
}
