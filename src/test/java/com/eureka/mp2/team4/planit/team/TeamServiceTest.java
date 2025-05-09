package com.eureka.mp2.team4.planit.team;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.service.TeamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDto teamDto;
    private TeamRequestDto teamRequestDto;

    @BeforeEach
    void setUp() {
        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(UUID.randomUUID().toString());
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 팀 설명");
    }

    @Test
    void testRegisterTeamFail() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).registerTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertTrue(response.getMessage().contains("팀 등록에 실패"));
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
    }

    @Test
    void testRegisterTeamSuccess() {
        // Given
        doNothing().when(teamMapper).registerTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(teamRequestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertTrue(response.getMessage().contains("성공적으로 등록"));
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
    }

    @Test
    void testUpdateTeamFail() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).updateTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.updateTeam(teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertTrue(response.getMessage().contains("팀 수정에 실패"));
        verify(teamMapper, times(1)).updateTeam(any(TeamDto.class));
    }

    @Test
    void testUpdateTeamSuccess() {
        // Given
        doNothing().when(teamMapper).updateTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.updateTeam(teamRequestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertTrue(response.getMessage().contains("성공적으로 수정"));
        verify(teamMapper, times(1)).updateTeam(any(TeamDto.class));
    }

    @Test
    void testDeleteTeamFail() {
        // Given
        String teamId = UUID.randomUUID().toString();
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).deleteTeam(any(String.class));

        // When
        ApiResponse response = teamService.deleteTeam(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertTrue(response.getMessage().contains("팀 삭제가 실패"));
        verify(teamMapper, times(1)).deleteTeam(any(String.class));
    }

    @Test
    void testDeleteTeamSuccess() {
        // Given
        String teamId = UUID.randomUUID().toString();
        doNothing().when(teamMapper).deleteTeam(any(String.class));

        // When
        ApiResponse response = teamService.deleteTeam(teamId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertTrue(response.getMessage().contains("성공적으로 삭제"));
        verify(teamMapper, times(1)).deleteTeam(any(String.class));
    }
}
