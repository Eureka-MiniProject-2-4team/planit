package com.eureka.mp2.team4.planit.team;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.controller.TeamController;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private TeamDto teamDto;
    private TeamRequestDto teamRequestDto;  // TeamRequestDto 추가
    private String teamId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        objectMapper = new ObjectMapper();
        teamId = UUID.randomUUID().toString();

        // 테스트용 TeamDto 생성
        teamDto = new TeamDto();
        teamDto.setId(teamId);
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 팀 설명");

        // 테스트용 TeamRequestDto 생성
        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(teamId);
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 팀 설명");
    }

    // TODO : CREATE
    @Test
    void testRegisterTeamFail() throws Exception {
        // Given

        // TeamRequestDto 타입으로 변경
        when(teamService.registerTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(REGISTER_TEAM_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAM_FAIL));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).registerTeam(any(TeamRequestDto.class));  // TeamRequestDto 타입으로 변경
    }

    @Test
    void testRegisterTeamSuccess() throws Exception {
        // Given
        // TeamRequestDto 타입으로 변경
        when(teamService.registerTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(REGISTER_TEAM_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAM_SUCCESS));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).registerTeam(any(TeamRequestDto.class));  // TeamRequestDto 타입으로 변경
    }

    // TODO : READ
    @Test
    void testGetTeamFail() throws Exception {
        // Given
        when(teamService.getTeamById(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(GET_TEAM_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/{id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(GET_TEAM_FAIL))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).getTeamById(eq(teamId));
    }

    @Test
    void testGetTeamWithInvalidId() throws Exception {
        // Given: 잘못된 형식의 ID를 사용하는 경우
        String invalidId = "invalid-uuid-format";
        when(teamService.getTeamById(eq(invalidId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(NOT_FOUND_ID)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(NOT_FOUND_ID));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).getTeamById(eq(invalidId));
    }

    @Test
    void testGetTeamSuccess() throws Exception {
        // Given
        when(teamService.getTeamById(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(GET_TEAM_SUCCESS)
                        .data(teamDto)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/{id}", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_TEAM_SUCCESS))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(teamId))
                .andExpect(jsonPath("$.data.teamName").value("테스트 팀"))
                .andExpect(jsonPath("$.data.description").value("테스트 팀 설명"));

        // 서비스 메서드 호출 검증
        verify(teamService, times(1)).getTeamById(eq(teamId));
    }

    // TODO : UPDATE
    @Test
    void testUpdateTeamFail() throws Exception {
        // Given

        // TeamRequestDto 타입으로 변경
        when(teamService.updateTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(UPDATE_TEAM_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{id}", teamRequestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(UPDATE_TEAM_FAIL));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).updateTeam(any(TeamRequestDto.class));  // TeamRequestDto 타입으로 변경
    }

    @Test
    void testUpdateTeamSuccess() throws Exception {
        // Given
        when(teamService.updateTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(UPDATE_TEAM_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{id}", teamRequestDto.getId())  // POST → PUT으로 변경
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(UPDATE_TEAM_SUCCESS));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).updateTeam(any(TeamRequestDto.class));
    }

    // TODO : DELETE
    @Test
    void testDeleteTeamFail() throws Exception {
        // Given
        String teamId = teamRequestDto.getId();

        when(teamService.deleteTeam(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(DELETE_TEAM_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{id}", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAM_FAIL));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).deleteTeam(eq(teamId));
    }

    @Test
    void testDeleteTeamSuccess() throws Exception {
        // Given
        String teamId = teamRequestDto.getId();
        when(teamService.deleteTeam(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(DELETE_TEAM_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{id}", teamId))  // POST → DELETE로 변경, 본문 제거
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAM_SUCCESS));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).deleteTeam(eq(teamId));
    }
}
