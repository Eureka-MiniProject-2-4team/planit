package com.eureka.mp2.team4.planit.team;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.controller.TeamController;
import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamController).build();
        objectMapper = new ObjectMapper();

        // 테스트용 TeamDto 생성
        teamDto = new TeamDto();
//        teamDto.setId(UUID.randomUUID());
        teamDto.setTeamName("테스트 팀");
        teamDto.setDescription("테스트 팀 설명");

        // 테스트용 TeamRequestDto 생성
        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(UUID.randomUUID());
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 팀 설명");
    }

    @Test
    void testRegisterTeamFail() throws Exception {
        // Given
        String errorMessage = "팀 등록에 실패했습니다: 중복된 팀 이름입니다.";
        // TeamRequestDto 타입으로 변경
        when(teamService.registerTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(errorMessage)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(errorMessage));

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
                        .message("팀이 성공적으로 등록되었습니다.")
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀이 성공적으로 등록되었습니다."));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).registerTeam(any(TeamRequestDto.class));  // TeamRequestDto 타입으로 변경
    }

    @Test
    void testUpdateTeamFail() throws Exception {
        // Given
        String errorMessage = "팀 수정에 실패했습니다: ";
        // TeamRequestDto 타입으로 변경
        when(teamService.updateTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(errorMessage)
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{id}", teamRequestDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))  // teamRequestDto 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(errorMessage));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).updateTeam(any(TeamRequestDto.class));  // TeamRequestDto 타입으로 변경
    }

    @Test
    void testUpdateTeamSuccess() throws Exception {
        // Given
        when(teamService.updateTeam(any(TeamRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("팀이 성공적으로 수정되었습니다.")
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{id}", teamRequestDto.getId())  // POST → PUT으로 변경
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀이 성공적으로 수정되었습니다."));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).updateTeam(any(TeamRequestDto.class));
    }

    @Test
    void testDeleteTeamFail() throws Exception {
        // Given
        UUID teamId = teamRequestDto.getId();
        String errorMessage = "팀 삭제에 실패했습니다: 해당 ID의 팀이 존재하지 않습니다.";
        when(teamService.deleteTeam(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(errorMessage)
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{id}", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(errorMessage));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).deleteTeam(eq(teamId));
    }

    @Test
    void testDeleteTeamSuccess() throws Exception {
        // Given
        UUID teamId = teamRequestDto.getId();
        when(teamService.deleteTeam(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("팀이 성공적으로 삭제되었습니다.")
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{id}", teamId))  // POST → DELETE로 변경, 본문 제거
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀이 성공적으로 삭제되었습니다."));

        // 서비스 메서드 호출 확인
        verify(teamService, times(1)).deleteTeam(eq(teamId));
    }
}
