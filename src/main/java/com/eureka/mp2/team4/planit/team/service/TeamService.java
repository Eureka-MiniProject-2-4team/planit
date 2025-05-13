package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;

public interface TeamService {

    // Create
    ApiResponse registerTeam(String userId, TeamRequestDto teamRequestDto);

    // Read
    ApiResponse getTeamById(String teamId);

    // Update
    ApiResponse updateTeam(TeamRequestDto teamRequestDto);

    // Delete
    ApiResponse deleteTeam(String teamId);

    boolean isTeamMember(String userId, String teamId);

    boolean isTeamLeader(String userId, String teamId);
}
