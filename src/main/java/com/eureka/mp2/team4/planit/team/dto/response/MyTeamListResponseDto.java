package com.eureka.mp2.team4.planit.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyTeamListResponseDto {
    private List<MyTeamResponseDto> myTeamList;
}
