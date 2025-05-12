package com.eureka.mp2.team4.planit.team.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyTeamListResponseDto {
    private List<MyTeamResponseDto> myTeamList;
}
