package com.eureka.mp2.team4.planit.team.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("")
    public ResponseEntity<ApiResponse> registerTeam(@RequestBody TeamRequestDto teamRequestDto) {
        return ResponseEntity.ok(teamService.registerTeam(teamRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateTeam(@PathVariable("id") String id, @RequestBody TeamRequestDto teamRequestDto) {
        teamRequestDto.setId(id);
        return ResponseEntity.ok(teamService.updateTeam(teamRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(@PathVariable("id") String id) {
        return ResponseEntity.ok(teamService.deleteTeam(id));
    }
}
