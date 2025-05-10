package com.eureka.mp2.team4.planit.team;

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

import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDto teamDto;
    private TeamRequestDto teamRequestDto;
    private String teamId;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID().toString();

        teamDto = new TeamDto();
        teamDto.setId(teamId);
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 설명");

        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(teamId);
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 팀 설명");
    }

    // TODO : CREATE
    @Test
    void testRegisterTeamFail() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).registerTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAM_FAIL, response.getMessage());
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
        assertEquals(REGISTER_TEAM_SUCCESS, response.getMessage());
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
    }

    // READ
    @Test
    void testGetTeamByIdFailNotFound() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = teamService.getTeamById(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAM_FAIL, response.getMessage());
        assertNull(response.getData());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
    }

    @Test
    void testGetTeamByIdFailException() {
        // Given
        when(teamMapper.findTeamById(eq(teamId)))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // When
        ApiResponse response = teamService.getTeamById(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAM_FAIL, response.getMessage());
        assertNull(response.getData());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
    }

    @Test
    void testGetTeamSuccess() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // When
        ApiResponse response = teamService.getTeamById(teamId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_TEAM_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(teamDto, response.getData());

        // 메서드 호출 검증
        verify(teamMapper, times(2)).findTeamById(eq(teamId));
    }

    // TODO : UPDATE
    @Test
    void testUpdateTeamFail() {
        // Given
        // 예외 발생 전 findTeamById 모킹
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // updateTeam에서 예외 발생
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).updateTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.updateTeam(teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(UPDATE_TEAM_FAIL, response.getMessage());

        // 두 메서드 모두 호출 확인
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(teamMapper, times(1)).updateTeam(any(TeamDto.class));
    }

    @Test
    void testUpdateTeamFailNotFound() {
        // Given
        // 팀을 찾지 못했을 때
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = teamService.updateTeam(teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(UPDATE_TEAM_FAIL, response.getMessage());

        // findTeamById만 호출, updateTeam은 호출되지 않음
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(teamMapper, never()).updateTeam(any(TeamDto.class));
    }

    @Test
    void testUpdateTeamSuccess() {
        // Given
        // 1. findTeamById 모킹 - 팀을 찾았을 때
        TeamDto existingTeam = new TeamDto();
        existingTeam.setId(teamId);
        existingTeam.setTeamName("기존 팀명");
        existingTeam.setDescription("기존 설명");

        when(teamMapper.findTeamById(eq(teamId))).thenReturn(existingTeam);

        // 2. updateTeam 모킹
        doNothing().when(teamMapper).updateTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.updateTeam(teamRequestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_TEAM_SUCCESS, response.getMessage());

        // 두 메서드 모두 호출 확인
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(teamMapper, times(1)).updateTeam(any(TeamDto.class));
    }

    // TODO : DELETE
    @Test
    void testDeleteTeamFail() {
        // Given
        // 1. 팀을 찾음
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // 2. 삭제 시 예외 발생
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).deleteTeam(eq(teamId));

        // When
        ApiResponse response = teamService.deleteTeam(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(DELETE_TEAM_FAIL, response.getMessage());

        // 두 메서드 모두 호출 확인
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(teamMapper, times(1)).deleteTeam(eq(teamId));
    }

    @Test
    void testDeleteTeamSuccess() {
        // Given
        // 1. 팀을 찾음
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // 2. 삭제 성공
        doNothing().when(teamMapper).deleteTeam(eq(teamId));

        // When
        ApiResponse response = teamService.deleteTeam(teamId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(DELETE_TEAM_SUCCESS, response.getMessage());

        // 두 메서드 모두 호출 확인
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(teamMapper, times(1)).deleteTeam(eq(teamId));
    }
}
