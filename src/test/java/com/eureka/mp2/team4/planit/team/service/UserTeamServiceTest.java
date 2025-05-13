package com.eureka.mp2.team4.planit.team.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.response.MyTeamResponseDto;
import com.eureka.mp2.team4.planit.team.mapper.TeamMapper;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;

@ExtendWith(MockitoExtension.class)
public class UserTeamServiceTest {

    @Mock
    private UserTeamMapper userTeamMapper;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private UserTeamServiceImpl userTeamService;

    private String userId;
    private String teamId;
    private UserTeamRequestDto userTeamRequestDto;
    private UserTeamDto userTeamDto;
    private TeamDto teamDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();
        teamId = UUID.randomUUID().toString();

        userTeamRequestDto = new UserTeamRequestDto();
        userTeamRequestDto.setUserId(userId);
        userTeamRequestDto.setTeamId(teamId);

        userTeamDto = UserTeamDto.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .teamId(teamId)
                .status("대기")
                .role("팀원")
                .build();

        teamDto = new TeamDto();
        teamDto.setId(teamId);
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 설명");
    }

    // CREATE
    @Test
    @DisplayName("팀원 초대 성공 테스트")
    void testInviteTeamMemberSuccess() {
        // Given
        doNothing().when(userTeamMapper).registerTeamMember(any(UserTeamDto.class));

        // When
        ApiResponse response = userTeamService.inviteTeamMember(userTeamRequestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(REGISTER_TEAM_MEMBER_SUCCESS, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).registerTeamMember(any(UserTeamDto.class));
    }

    @Test
    @DisplayName("팀원 초대 실패 테스트")
    void testInviteTeamMemberFail() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(userTeamMapper).registerTeamMember(any(UserTeamDto.class));

        // When
        ApiResponse response = userTeamService.inviteTeamMember(userTeamRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAM_MEMBER_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).registerTeamMember(any(UserTeamDto.class));
    }

    // READ
    @Test
    @DisplayName("내 팀 목록 조회 성공 테스트")
    void testGetMyTeamListSuccess() {
        // Given
        List<UserTeamDto> userTeamDtoList = Arrays.asList(userTeamDto);
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(userTeamDtoList);
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // When
        ApiResponse response = userTeamService.getMyTeamList(userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_TEAMLIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof List);
        List<?> teamList = (List<?>) response.getData();
        assertEquals(1, teamList.size());
        assertTrue(teamList.get(0) instanceof MyTeamResponseDto);

        // 메서드 호출 검증
        verify(userTeamMapper, times(2)).getMyTeamList(eq(userId));
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
    }

    @Test
    @DisplayName("내 팀 목록 조회 실패 테스트 - 팀 없음")
    void testGetMyTeamListFailNotFound() {
        // Given
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(null);

        // When
        ApiResponse response = userTeamService.getMyTeamList(userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAMLIST_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(teamMapper, never()).findTeamById(anyString());
    }

    @Test
    @DisplayName("내 팀 목록 조회 실패 테스트 - 예외 발생")
    void testGetMyTeamListFailException() {
        // Given
        when(userTeamMapper.getMyTeamList(eq(userId))).thenThrow(new RuntimeException("DB 에러"));

        // When
        ApiResponse response = userTeamService.getMyTeamList(userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAMLIST_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(teamMapper, never()).findTeamById(anyString());
    }

    @Test
    @DisplayName("초대받은 팀 목록 조회 성공 테스트")
    void testGetMyInvitedListSuccess() {
        // Given
        List<UserTeamDto> userTeamDtoList = Arrays.asList(userTeamDto);
        when(userTeamMapper.getMyInvitedList(eq(userId))).thenReturn(userTeamDtoList);
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);

        // When
        ApiResponse response = userTeamService.getMyInvitedList(userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_INVITED_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof List);
        List<?> teamList = (List<?>) response.getData();
        assertEquals(1, teamList.size());
        assertTrue(teamList.get(0) instanceof MyTeamResponseDto);

        // 메서드 호출 검증
        verify(userTeamMapper, times(2)).getMyInvitedList(eq(userId));
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
    }

    @Test
    @DisplayName("내 팀 목록 조회 실패 테스트 - 팀 없음")
    void testGetMyInvitedListFailNotFound() {
        // Given
        when(userTeamMapper.getMyInvitedList(eq(userId))).thenReturn(null);

        // When
        ApiResponse response = userTeamService.getMyInvitedList(userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_INVITED_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).getMyInvitedList(eq(userId));
        verify(teamMapper, never()).findTeamById(anyString());
    }

    @Test
    @DisplayName("내 팀 목록 조회 실패 테스트 - 예외 발생")
    void testGetMyInvitedListFailException() {
        // Given
        when(userTeamMapper.getMyInvitedList(eq(userId))).thenThrow(new RuntimeException("DB 에러"));

        // When
        ApiResponse response = userTeamService.getMyInvitedList(userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_INVITED_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).getMyInvitedList(eq(userId));
        verify(teamMapper, never()).findTeamById(anyString());
    }

    // UPDATE
    @Test
    @DisplayName("팀 가입 수락 성공 테스트")
    void testAcceptTeamJoinSuccess() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);
        doNothing().when(userTeamMapper).acceptTeamJoin(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.acceptTeamJoin(teamId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(ACCEPT_TEAM_JOIN_SUCCESS, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, times(1)).acceptTeamJoin(eq(teamId), eq(userId));
    }

    @Test
    @DisplayName("팀 가입 수락 실패 테스트 - 팀 없음")
    void testAcceptTeamJoinFailTeamNotFound() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = userTeamService.acceptTeamJoin(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(ACCEPT_TEAM_JOIN_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, never()).acceptTeamJoin(anyString(), anyString());
    }

    @Test
    @DisplayName("팀 가입 수락 실패 테스트 - 예외 발생")
    void testAcceptTeamJoinFailException() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);
        doThrow(new RuntimeException("DB 에러")).when(userTeamMapper).acceptTeamJoin(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.acceptTeamJoin(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(ACCEPT_TEAM_JOIN_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, times(1)).acceptTeamJoin(eq(teamId), eq(userId));
    }

    // DELETE
    @Test
    @DisplayName("팀원 강퇴 성공 테스트")
    void testDeleteTeamMemberSuccess() {
        // Given
        when(userTeamMapper.findByTeamIdAndUserId(eq(teamId), eq(userId))).thenReturn(userTeamDto);
        doNothing().when(userTeamMapper).deleteTeamMember(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.deleteTeamMember(teamId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(DELETE_TEAM_MEMBER_SUCCESS, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).findByTeamIdAndUserId(eq(teamId), eq(userId));
        verify(userTeamMapper, times(1)).deleteTeamMember(eq(teamId), eq(userId));
    }

    @Test
    @DisplayName("팀원 강퇴 실패 테스트 - 팀원 없음")
    void testDeleteTeamMemberFailNotFound() {
        // Given
        when(userTeamMapper.findByTeamIdAndUserId(eq(teamId), eq(userId))).thenReturn(null);

        // When
        ApiResponse response = userTeamService.deleteTeamMember(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(DELETE_TEAM_MEMBER_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).findByTeamIdAndUserId(eq(teamId), eq(userId));
        verify(userTeamMapper, never()).deleteTeamMember(anyString(), anyString());
    }

    @Test
    @DisplayName("팀원 강퇴 실패 테스트 - 예외 발생")
    void testDeleteTeamMemberFailException() {
        // Given
        when(userTeamMapper.findByTeamIdAndUserId(eq(teamId), eq(userId))).thenReturn(userTeamDto);
        doThrow(new RuntimeException("DB 에러")).when(userTeamMapper).deleteTeamMember(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.deleteTeamMember(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(DELETE_TEAM_MEMBER_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(userTeamMapper, times(1)).findByTeamIdAndUserId(eq(teamId), eq(userId));
        verify(userTeamMapper, times(1)).deleteTeamMember(eq(teamId), eq(userId));
    }

    @Test
    @DisplayName("팀 가입 거부 성공 테스트")
    void testDenyTeamMemberSuccess() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);
        doNothing().when(userTeamMapper).deleteTeamMember(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.denyTeamMember(teamId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(QUIT_TEAM_MEMBER_SUCCESS, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, times(1)).deleteTeamMember(eq(teamId), eq(userId));
    }

    @Test
    @DisplayName("팀 가입 거부 실패 테스트 - 팀 없음")
    void testDenyTeamMemberFailTeamNotFound() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = userTeamService.denyTeamMember(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(QUIT_TEAM_MEMBER_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, never()).deleteTeamMember(anyString(), anyString());
    }

    @Test
    @DisplayName("팀 가입 거부 실패 테스트 - 예외 발생")
    void testDenyTeamMemberFailException() {
        // Given
        when(teamMapper.findTeamById(eq(teamId))).thenReturn(teamDto);
        doThrow(new RuntimeException("DB 에러")).when(userTeamMapper).deleteTeamMember(eq(teamId), eq(userId));

        // When
        ApiResponse response = userTeamService.denyTeamMember(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(QUIT_TEAM_MEMBER_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(teamMapper, times(1)).findTeamById(eq(teamId));
        verify(userTeamMapper, times(1)).deleteTeamMember(eq(teamId), eq(userId));
    }
}