package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;
import static com.eureka.mp2.team4.planit.team.constants.Team_Role.MEMBER;
import static com.eureka.mp2.team4.planit.team.constants.Team_Status.JOINED;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamMapper teamMapper;
    @Mock
    private UserTeamMapper userTeamMapper;

    @InjectMocks
    private TeamServiceImpl teamService;

    private TeamDto teamDto;
    private TeamRequestDto teamRequestDto;
    private UserTeamRequestDto userTeamRequestDto;
    private UserTeamDto userTeamDto;
    private String teamId;
    private String userId;

    @BeforeEach
    void setUp() {
        teamId = UUID.randomUUID().toString();
        userId = UUID.randomUUID().toString();

        userTeamRequestDto = new UserTeamRequestDto();
        userTeamRequestDto.setUserId(userId);
        userTeamRequestDto.setTeamId(teamId);

        userTeamDto = new UserTeamDto();
        userTeamDto.setId(UUID.randomUUID().toString());
        userTeamDto.setUserId(userId);
        userTeamDto.setUserId(teamId);
        userTeamDto.setStatus(JOINED);
        userTeamDto.setRole(MEMBER);

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
    @DisplayName("팀 등록 실패 테스트 - 팀 등록 에러")
    void testRegisterTeamFailOnTeamRegistration() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamMapper).registerTeam(any(TeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(userId, teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAM_FAIL, response.getMessage());

        // teamMapper는 호출되었지만, userTeamMapper는 호출되지 않았는지 확인
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
        verify(userTeamMapper, never()).registerTeamMember(any(UserTeamDto.class));
    }

    @Test
    @DisplayName("팀 등록 실패 테스트 - 멤버 등록 에러")
    void testRegisterTeamFailOnMemberRegistration() {
        // Given
        doNothing().when(teamMapper).registerTeam(any(TeamDto.class));
        doThrow(new RuntimeException("DB 에러")).when(userTeamMapper).registerTeamMember(any(UserTeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(userId, teamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAM_FAIL, response.getMessage());

        // 두 매퍼 모두 호출되었는지 확인
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
        verify(userTeamMapper, times(1)).registerTeamMember(any(UserTeamDto.class));
    }

    @Test
    @DisplayName(" 팀 등록 성공 테스트")
    void testRegisterTeamSuccess() {
        // Given
        // teamMapper와 userTeamMapper가 예외를 발생시키지 않도록 설정
        doNothing().when(teamMapper).registerTeam(any(TeamDto.class));
        doNothing().when(userTeamMapper).registerTeamMember(any(UserTeamDto.class));

        // When
        ApiResponse response = teamService.registerTeam(userId, teamRequestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(REGISTER_TEAM_SUCCESS, response.getMessage());

        // TeamDto와 UserTeamDto가 생성되어 전달되었는지 확인
        verify(teamMapper, times(1)).registerTeam(any(TeamDto.class));
        verify(userTeamMapper, times(1)).registerTeamMember(any(UserTeamDto.class));
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
