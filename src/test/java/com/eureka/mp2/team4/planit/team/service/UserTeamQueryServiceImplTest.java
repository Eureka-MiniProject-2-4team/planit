package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserTeamQueryServiceImplTest {

    @InjectMocks
    private UserTeamQueryServiceImpl userTeamQueryService;

    @Mock
    private UserTeamMapper userTeamMapper;

    @Test
    @DisplayName("팀장인 팀이 존재할 경우 true 반환")
    void isUserTeamLeader_whenTeamsExist_returnsTrue() {
        // given
        String userId = "user-123";
        List<TeamDto> teamList = List.of(
                new TeamDto("team-1", "팀 A", "소개1", null, null)
        );
        when(userTeamMapper.findTeamByLeaderId(userId)).thenReturn(teamList);

        // when
        boolean result = userTeamQueryService.isUserTeamLeader(userId);

        // then
        assertEquals(true, result);
        verify(userTeamMapper).findTeamByLeaderId(userId);
    }

    @Test
    @DisplayName("팀장인 팀이 없을 경우 false 반환")
    void isUserTeamLeader_whenNoTeams_returnsFalse() {
        // given
        String userId = "user-456";
        when(userTeamMapper.findTeamByLeaderId(userId)).thenReturn(List.of());

        // when
        boolean result = userTeamQueryService.isUserTeamLeader(userId);

        // then
        assertFalse(result);
        verify(userTeamMapper).findTeamByLeaderId(userId);
    }
}