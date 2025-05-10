package com.eureka.mp2.team4.planit.team.mapper;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import org.apache.ibatis.annotations.Mapper;

// 팀 CRUD
@Mapper
public interface TeamMapper {
    // Create
    void registerTeam(TeamDto teamDto);

    // Read
    TeamDto findTeamById(String id);

    // Update
    void updateTeam(TeamDto teamDto);

    // Delete
    void deleteTeam(String id);
}
