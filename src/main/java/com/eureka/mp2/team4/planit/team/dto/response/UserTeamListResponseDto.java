package com.eureka.mp2.team4.planit.team.dto.response;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserTeamListResponseDto {
    private List<UserTeamDto> teamList;
}
