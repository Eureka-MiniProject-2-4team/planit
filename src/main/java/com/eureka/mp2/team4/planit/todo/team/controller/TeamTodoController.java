package com.eureka.mp2.team4.planit.todo.team.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.annotations.TeamCheck;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoService;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@Tag(name = "팀 투두 CRUD API", description = "팀 투두 CRUD 기능")
public class TeamTodoController {
    private final TeamTodoService teamTodoService;
    private final TeamTodoUserService myTeamTodoService;

    @Operation(summary = "팀 투두 생성", description = "팀장만 팀 투두 등록 가능, 권한 적용")
    @TeamCheck(onlyLeader = true)
    @PostMapping("/{teamId}/todo")
    public ResponseEntity<ApiResponse> createTeamTodo(@PathVariable String teamId,
                                                      @RequestBody TeamTodoRequestDto teamTodoRequestDto){
        teamTodoRequestDto.setTeamId(teamId);
        return ResponseEntity.ok(teamTodoService.createTeamTodo(teamTodoRequestDto));
    }

    // 특정 팀의 모든 todoList 목록, Parameter 사용
    @Operation(summary = "팀 투두 리스트 조회", description = "특정 팀의 모든 투두 리스트 조회")
    @TeamCheck
    @GetMapping("/{teamId}/todo/list")
    public ResponseEntity<ApiResponse> getTeamTodoList(@PathVariable String teamId) {
        return ResponseEntity.ok(teamTodoService.getTeamTodoList(teamId));
    }

    // 특정 투두 정보, PathVariavle 사용
    @Operation(summary = "팀 투두 상세 조회", description = "특정 팀 투두를 상세 조회")
    @TeamCheck
    @GetMapping("{teamId}/todo/{teamTodoId}")
    public ResponseEntity<ApiResponse> getTeamTodoById(@PathVariable("teamId") String teamId, @PathVariable("teamTodoId") String teamTodoId) {
        return ResponseEntity.ok(teamTodoService.getTeamTodoById(teamTodoId));
    }

    // 캘린더 페이지 로드 시 실행
    @Operation(summary = "나의 팀 투두 전체 조회", description = "내가 속한 팀의 모든 투두 나의 상태로 가져오기, 추후에 캘린더에서 모든 팀의 모든 투두 가져오기와 연계")
//    @TeamCheck
    @GetMapping("{teamId}/todo/my/list")
    public ResponseEntity<ApiResponse> getMyTeamListTodoList(@PathVariable("teamId") String teamId, @AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(myTeamTodoService.getMyTeamListAndTodoList(userId)); // teamId 로 해당 팀의 투두 id 리스트 싹 담아와서 -> userId 로 각 투두 id 리스트에 대한 상태 표시
    }

    @Operation(summary = "나의 팀 투두 상세 조회", description = "팀에서 나에게 부여한 투두를 나의 진행도에 맞게 개인별 확인")
    @TeamCheck
    @GetMapping("{teamId}/todo/my/{teamTodoUserId}")
    public ResponseEntity<ApiResponse> getMyTeamTodoDetail(@PathVariable("teamId") String teamId,
                                                           @PathVariable("teamTodoUserId") String teamTodoUserId,
                                                           @AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        String userId = planitUserDetails.getUsername();
        return ResponseEntity.ok(myTeamTodoService.getMyTeamTodoDetail(teamId, teamTodoUserId, userId)); // teamTodoUserId 와 userId 를 검색조건으로 개별 투두 id 받아옴
    }

    @Operation(summary = "팀 투두 수정", description = "팀장만 팀 투두 수정 가능, 권한 적용")
    @TeamCheck(onlyLeader = true)
    @PutMapping("/{teamId}/todo/{todoId}")
    public ResponseEntity<ApiResponse> updateTeamTodo(@PathVariable("teamId") String teamId,
                                                      @RequestBody TeamTodoRequestDto teamTodoRequestDto,
                                                      @PathVariable("todoId") String teamTodoId){
        teamTodoRequestDto.setTeamId(teamId);
        teamTodoRequestDto.setId(teamTodoId);
        return ResponseEntity.ok(teamTodoService.updateTeamTodo(teamTodoRequestDto));
    }

    @Operation(summary = "나의 팀 투두 상태 변경", description = "0과 1로 나의 팀 투두 진행 미완/완료 표현")
    @TeamCheck
    @PutMapping("/{teamId}/todo/my/{teamTodoUserId}")
    public ResponseEntity<ApiResponse> updateMyTeamTodo(@PathVariable("teamId") String teamId,
                                                        @PathVariable("teamTodoUserId") String teamTodoUserId,
                                                        @RequestBody MyTeamTodoRequestDto myTeamTodoRequestDto,
                                                        @AuthenticationPrincipal PlanitUserDetails planitUserDetails) {
        myTeamTodoRequestDto.setTeamTodoId(teamTodoUserId);
        myTeamTodoRequestDto.setUserId(planitUserDetails.getUsername());
        return ResponseEntity.ok(myTeamTodoService.updateMyTeamTodo(myTeamTodoRequestDto));
    }

    @Operation(summary = "팀 투두 삭제", description = "팀장만 팀 투두 삭제 가능, 권한 적용")
    @TeamCheck(onlyLeader = true)
    @DeleteMapping("/{teamId}/todo/{todoId}")
    public ResponseEntity<ApiResponse> deleteTeamTodo(@PathVariable("teamId") String teamId, // 일관성 위해, 실제 필요성은 X
                                                      @PathVariable("todoId") String teamTodoId){
        return ResponseEntity.ok(teamTodoService.deleteTeamTodo(teamTodoId));
    }
}
