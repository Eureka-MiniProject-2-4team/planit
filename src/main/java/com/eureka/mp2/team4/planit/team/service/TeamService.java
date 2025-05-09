package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;

import java.util.UUID;

public interface TeamService {

    // Create
    ApiResponse registerTeam(TeamRequestDto teamRequestDto);

    // Read

    // Update
    ApiResponse updateTeam(TeamRequestDto teamRequestDto);

    // Delete
    ApiResponse deleteTeam(String teamId);
}
