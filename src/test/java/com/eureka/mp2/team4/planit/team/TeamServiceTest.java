package com.eureka.mp2.team4.planit.team;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.response.TeamResponseDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.service.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDto teamDto;
    private UUID teamId;
//    private TeamResponseDto responseDto;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID();

        teamDto = new TeamDto();
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 팀 설명");

//        responseDto = new TeamResponseDto();
//        responseDto.setId(teamId);
//        responseDto.setTeamname("테스트 팀");
//        responseDto.setDescription("테스트 팀 설명");
    }

    @Test
    void testRegisterFail() {
        // Given
        when(teamMapper.registerTeam(any(TeamDto.class));).thenReturn(0);

        // When
        TeamDto result = teamService.registerTeam(teamDto);

        // Then
        assertNull(result);
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
        verify(teamMapper, never()).getTeamById(any(UUID.class));
    }

    @Test
    void testRegisterSuccess() {
        // Given
        when(teamMapper.insertTeam(any(TeamDto.class))).thenReturn(1);
        when(teamMapper.getTeamById(any(UUID.class))).thenReturn(teamDto);

        // When
        TeamDto result = teamService.registerTeam(teamDto);

        // Then
        assertNotNull(result);
        assertEquals(teamDto.getTeamname(), result.getTeamname());
        assertEquals(teamDto.getDescription(), result.getDescription());

        verify(teamMapper, times(1)).insertTeam(any(TeamDto.class));
        verify(teamMapper, times(1)).getTeamById(any(UUID.class));
    }

}
