package com.eureka.mp2.team4.planit.team.mapper;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;

import java.util.UUID;

public interface TeamMapper {
    // Create
    void registerTeam(TeamDto teamDto);

    // Read

    // Update
    void updateTeam(TeamDto teamDto);

    // Delete
    void deleteTeam(UUID id);
}
