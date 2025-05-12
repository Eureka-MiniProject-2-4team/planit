package com.eureka.mp2.team4.planit.todo.team.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@Tag(name = "팀 투두 CRUD API", description = "팀 투두 CRUD 기능")
public class TeamTodoController {
    private final TeamTodoService teamTodoService;

    @Operation(summary = "팀 투두 생성", description = "팀장만 팀 투두 등록 가능, 아직 권한 미적용")
    @PostMapping("/{teamId}/todo")
    public ResponseEntity<ApiResponse> createTeamTodo(@PathVariable String teamId,
                                                      @RequestBody TeamTodoRequestDto teamTodoRequestDto){
        teamTodoRequestDto.setTeamId(teamId);
        return ResponseEntity.ok(teamTodoService.createTeamTodo(teamTodoRequestDto));
    }

    // 특정 팀의 모든 todoList 목록, Parameter 사용
    @Operation(summary = "팀 투두 리스트 조회", description = "특정 팀의 모든 투두 리스트 조회")
    @GetMapping("/{teamId}/todo/list")
    public ResponseEntity<ApiResponse> getTeamTodoList(@PathVariable String teamId) {
        return ResponseEntity.ok(teamTodoService.getTeamTodoList(teamId));
    }

    // 특정 투두 정보, PathVariavle 사용
    @Operation(summary = "팀 투두 상세 조회", description = "특정 팀 투두를 상세 조회")
    @GetMapping("/todo/{teamTodoId}")
    public ResponseEntity<ApiResponse> getTeamTodoById(@PathVariable String teamTodoId) {
        return ResponseEntity.ok(teamTodoService.getTeamTodoById(teamTodoId));
    }

    @Operation(summary = "팀 투두 수정", description = "팀장만 팀 투두 수정 가능, 아직 권한 미적용")
    @PutMapping("/{teamId}/todo/{todoId}")
    public ResponseEntity<ApiResponse> updateTeamTodo(@PathVariable("teamId") String teamId,
                                                      @RequestBody TeamTodoRequestDto teamTodoRequestDto,
                                                      @PathVariable("todoId") String teamTodoId){
        teamTodoRequestDto.setTeamId(teamId);
        teamTodoRequestDto.setId(teamTodoId);
        return ResponseEntity.ok(teamTodoService.updateTeamTodo(teamTodoRequestDto));
    }

    @Operation(summary = "팀 투두 삭제", description = "팀장만 팀 투두 삭제 가능, 아직 권한 미적용")
    @DeleteMapping("/{teamId}/todo/{todoId}")
    public ResponseEntity<ApiResponse> deleteTeamTodo(@PathVariable("teamId") String teamId, // 일관성 위해, 실제 필요성은 X
                                                      @PathVariable("todoId") String teamTodoId){
        return ResponseEntity.ok(teamTodoService.deleteTeamTodo(teamTodoId));
    }
}
