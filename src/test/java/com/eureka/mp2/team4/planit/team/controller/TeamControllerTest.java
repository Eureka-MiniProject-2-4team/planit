package com.eureka.mp2.team4.planit.team.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.WithMockPlanitUser;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.response.MyTeamResponseDto;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import com.eureka.mp2.team4.planit.team.service.UserTeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @MockitoBean
    private TeamService teamService;

    @MockitoBean
    private UserTeamService userTeamService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String userId;
    private String teamId;
    private TeamRequestDto teamRequestDto;
    private UserTeamRequestDto userTeamRequestDto;
    private PlanitUserDetails userDetails;


    @BeforeEach
    void setUp() {
        userId = "test-user-id";
        teamId = UUID.randomUUID().toString();

        // TeamRequestDto 설정
        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(teamId);
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 설명");

        // UserTeamRequestDto 설정
        userTeamRequestDto = new UserTeamRequestDto();
        userTeamRequestDto.setTeamId(teamId);
        userTeamRequestDto.setUserId(userId);
    }

    // CREATE
    @Test
    @DisplayName("팀 등록 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testRegisterTeamSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(REGISTER_TEAM_SUCCESS)
                .build();

        when(teamService.registerTeam(eq("test-user-id"), any(TeamRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/team")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAM_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).registerTeam(eq(userId), any(TeamRequestDto.class));
    }

    @Test
    @DisplayName("팀 등록 실패 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testRegisterTeamFail() throws Exception {
        // Given
        ApiResponse failResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message(REGISTER_TEAM_FAIL)
                .build();

        when(teamService.registerTeam(eq(userId), any(TeamRequestDto.class)))
                .thenReturn(failResponse);

        // When & Then
        mockMvc.perform(post("/api/team")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAM_FAIL));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).registerTeam(eq(userId), any(TeamRequestDto.class));
    }

    @Test
    @DisplayName("팀원 초대 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testInviteMemberSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(REGISTER_TEAM_MEMBER_SUCCESS)
                .build();

        when(userTeamService.inviteTeamMember(any(UserTeamRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/team/{teamId}/member/{userId}", teamId, userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userTeamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAM_MEMBER_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).inviteTeamMember(any(UserTeamRequestDto.class));
    }

    // READ
    @Test
    @DisplayName("팀 상세 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetTeamSuccess() throws Exception {
        // Given
        TeamDto teamDto = new TeamDto();
        teamDto.setId(teamId);
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 설명");

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(GET_TEAM_SUCCESS)
                .data(teamDto)
                .build();

        when(teamService.getTeamById(eq(teamId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/{id}", teamId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_TEAM_SUCCESS))
                .andExpect(jsonPath("$.data.id").value(teamId))
                .andExpect(jsonPath("$.data.teamName").value("테스트 팀"));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).getTeamById(eq(teamId));
    }

    @Test
    @DisplayName("나의 팀 리스트 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetMyTeamListSuccess() throws Exception {
        // Given
        List<MyTeamResponseDto> teamList = Arrays.asList(
                new MyTeamResponseDto(/* 필요한 파라미터 */)
        );

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(GET_MY_TEAMLIST_SUCCESS)
                .data(teamList)
                .build();

        when(userTeamService.getMyTeamList(eq(userId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/myteamlist")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_MY_TEAMLIST_SUCCESS))
                .andExpect(jsonPath("$.data").isArray());

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).getMyTeamList(eq(userId));
    }

    @Test
    @DisplayName("나의 초대 리스트 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetMyInviteListSuccess() throws Exception {
        // Given
        String userId = "test-user-id";

        List<MyTeamResponseDto> inviteList = Arrays.asList(
                new MyTeamResponseDto(/* 필요한 파라미터 */)
        );

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(GET_MY_INVITED_SUCCESS)
                .data(inviteList)
                .build();

        when(userTeamService.getMyInvitedList(eq(userId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/invitelist")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_MY_INVITED_SUCCESS))
                .andExpect(jsonPath("$.data").isArray());

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).getMyInvitedList(eq(userId));
    }

    @Test
    @DisplayName("나의 초대 리스트 조회 실패 테스트 - 권한 없음")
    void testGetMyInviteListUnauthorized() throws Exception {
        // When & Then - 인증 없이 요청
        mockMvc.perform(get("/api/team/invitelist")
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // 401 응답 기대

        // 서비스 메서드 호출되지 않음을 검증
        verify(userTeamService, never()).getMyInvitedList(any());
    }

    // UPDATE
    @Test
    @DisplayName("팀 정보 수정 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testUpdateTeamSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(UPDATE_TEAM_SUCCESS)
                .build();

        when(teamService.updateTeam(any(TeamRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(put("/api/team/{id}", teamId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(UPDATE_TEAM_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).updateTeam(any(TeamRequestDto.class));
    }

    @Test
    @DisplayName("팀 가입 수락 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testAcceptTeamJoinSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(ACCEPT_TEAM_JOIN_SUCCESS)
                .build();

        when(userTeamService.acceptTeamJoin(eq(teamId), eq(userId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(put("/api/team/{teamId}/member", teamId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(ACCEPT_TEAM_JOIN_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).acceptTeamJoin(eq(teamId), eq(userId));
    }

    // DELETE
    @Test
    @DisplayName("팀 삭제 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testDeleteTeamSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(DELETE_TEAM_SUCCESS)
                .build();

        when(teamService.deleteTeam(eq(teamId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/team/{id}", teamId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAM_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).deleteTeam(eq(teamId));
    }

    @Test
    @DisplayName("팀원 강퇴 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testDeleteMemberSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(DELETE_TEAM_MEMBER_SUCCESS)
                .build();

        when(userTeamService.deleteTeamMember(eq(teamId), eq(userId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/team/{teamId}/member/{userId}", teamId, userId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAM_MEMBER_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).deleteTeamMember(eq(teamId), eq(userId));
    }

    @Test
    @DisplayName("팀 가입 거절 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testDenyTeamJoinSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(QUIT_TEAM_MEMBER_SUCCESS)
                .build();

        when(userTeamService.denyTeamMember(eq(teamId), eq(userId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/team/{teamId}/member", teamId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(QUIT_TEAM_MEMBER_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(userTeamService, times(1)).denyTeamMember(eq(teamId), eq(userId));
    }
}
