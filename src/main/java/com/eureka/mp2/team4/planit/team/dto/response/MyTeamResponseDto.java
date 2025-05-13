package com.eureka.mp2.team4.planit.team.dto.response;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyTeamResponseDto {
    private TeamDto myTeam;
    private UserTeamDto myUserTeam;
}
