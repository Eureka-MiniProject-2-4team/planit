package com.eureka.mp2.team4.planit.team.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
@Tag(name="팀 자체의 CRUD API", description = "팀 자체의 CRUD 기능, *팀원 관리 API 는 별도로 존재 + 아직 유저 권한을 갖고있는지 체크하는 과정 미적용")
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "팀 새로 등록")
    @PostMapping("")
    public ResponseEntity<ApiResponse> registerTeam(@RequestBody TeamRequestDto teamRequestDto) {
        return ResponseEntity.ok(teamService.registerTeam(teamRequestDto));
    }

    @Operation(summary = "팀 상세 조회", description = "특정 팀의 모든 정보를 PathVariable : id 를 통해 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @Operation(summary = "팀 정보 수정", description = "팀장만 가능한 수정작업, 아직 팀장 권한 확인 미적용")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTeam(@PathVariable("id") String id, @RequestBody TeamRequestDto teamRequestDto) {
        teamRequestDto.setId(id);
        return ResponseEntity.ok(teamService.updateTeam(teamRequestDto));
    }

    @Operation(summary = "팀 삭제", description = "팀장만 가능한 삭제작업, 아직 팀장 권한 확인 미적용")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.deleteTeam(id));
    }
}
