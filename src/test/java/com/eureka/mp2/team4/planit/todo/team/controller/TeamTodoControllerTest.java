package com.eureka.mp2.team4.planit.todo.team.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.WithMockPlanitUser;
import com.eureka.mp2.team4.planit.team.dto.request.TeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.request.UserTeamRequestDto;
import com.eureka.mp2.team4.planit.team.dto.response.UserTeamResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.*;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoService;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamTodoController.class)
class TeamTodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeamTodoService teamTodoService;

    @MockitoBean
    private TeamTodoUserService myTeamTodoService;

    private String userId;
    private String teamId;
    private String todoId;
    private String teamTodoUserId;

    // 다양한 요청 DTO
    private TeamRequestDto teamRequestDto;
    private UserTeamRequestDto userTeamRequestDto;
    private TeamTodoRequestDto teamTodoRequestDto;
    private MyTeamTodoRequestDto myTeamTodoRequestDto;

    // 다양한 응답 DTO
//    private TeamResponseDto teamResponseDto;
    private UserTeamResponseDto userTeamResponseDto;
    private TeamTodoResponseDto teamTodoResponseDto;
    private MyTeamTodoDetailResponseDto myTeamTodoDetailResponseDto;
    private MyTeamListResponseDto myTeamListResponseDto;

    @BeforeEach
    void setUp() {
        // 기본 ID 값 설정
        userId = "test-user-id";
        teamId = UUID.randomUUID().toString();
        todoId = UUID.randomUUID().toString();
        teamTodoUserId = UUID.randomUUID().toString();

        // TeamRequestDto 설정
        teamRequestDto = new TeamRequestDto();
        teamRequestDto.setId(teamId);
        teamRequestDto.setTeamName("테스트 팀");
        teamRequestDto.setDescription("테스트 설명");

        // UserTeamRequestDto 설정
        userTeamRequestDto = new UserTeamRequestDto();
        userTeamRequestDto.setTeamId(teamId);
        userTeamRequestDto.setUserId(userId);

        // TeamTodoRequestDto 설정
        teamTodoRequestDto = new TeamTodoRequestDto();
        teamTodoRequestDto.setId(todoId);
        teamTodoRequestDto.setTeamId(teamId);
        teamTodoRequestDto.setTitle("테스트 할 일");
        teamTodoRequestDto.setContent("테스트 내용");

        // TeamTodoUserRequestDto 설정
        myTeamTodoRequestDto = new MyTeamTodoRequestDto();
        myTeamTodoRequestDto.setTeamTodoId(todoId);
        myTeamTodoRequestDto.setUserId(userId);
        myTeamTodoRequestDto.setCompleted(false);

        // UserTeamResponseDto 설정 // 아직 미구현

        // TeamTodoResponseDto 설정
        teamTodoResponseDto = new TeamTodoResponseDto();
        teamTodoResponseDto.setTitle("테스트 할 일");
        teamTodoResponseDto.setContent("테스트 내용");
        teamTodoResponseDto.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        teamTodoResponseDto.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // MyTeamTodoDetailResponseDto 설정
        myTeamTodoDetailResponseDto = new MyTeamTodoDetailResponseDto();
        myTeamTodoDetailResponseDto.setTitle("테스트 할 일");
        myTeamTodoDetailResponseDto.setContent("테스트 내용");
        myTeamTodoDetailResponseDto.setCompleted(false);
        myTeamTodoDetailResponseDto.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        // MyTeamListResponse 설정 (계층 구조)
        // 팀 내 할 일 정보 설정
        List<TeamTodoWithUserStatusResponseDto> todos = new ArrayList<>();
        TeamTodoWithUserStatusResponseDto todoWithStatus = new TeamTodoWithUserStatusResponseDto();
        todoWithStatus.setTodoId(todoId);
        todoWithStatus.setTitle("테스트 할 일");
        todoWithStatus.setContent("테스트 내용");
        todoWithStatus.setCompleted(false);
        todoWithStatus.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        todos.add(todoWithStatus);

        // 팀 정보 설정
        List<TeamWithTodoResponseDto> teams = new ArrayList<>();
        TeamWithTodoResponseDto teamWithTodos = new TeamWithTodoResponseDto();
        teamWithTodos.setTeamId(teamId);
        teamWithTodos.setTodos(todos);
        teams.add(teamWithTodos);

        // 최종 응답 설정
        myTeamListResponseDto = new MyTeamListResponseDto();
        myTeamListResponseDto.setTeams(teams);
    }

    @Test
    @DisplayName("팀 투두 생성 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testCreateTeamTodoSuccess() throws Exception {
        // Given
        TeamTodoRequestDto requestDto = teamTodoRequestDto;
        // requestDto 필드 세팅

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("팀 투두 생성 성공")
                .build();

        when(teamTodoService.createTeamTodo(any(TeamTodoRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/team/{teamId}/todo", teamId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀 투두 생성 성공"));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).createTeamTodo(any(TeamTodoRequestDto.class));
    }

    @Test
    @DisplayName("팀 투두 리스트 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetTeamTodoListSuccess() throws Exception {
        // Given
        List<TeamTodoDto> teamTodoDtoList = new ArrayList<>();
        // 테스트 데이터 추가
        TeamTodoDto teamTodoDto1 = new TeamTodoDto();
        teamTodoDto1.setId(UUID.randomUUID().toString());
        teamTodoDto1.setTeamId(teamId);
        teamTodoDto1.setTitle("팀 투두 1");
        teamTodoDto1.setContent("팀 투두 내용 1");
        teamTodoDto1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        teamTodoDto1.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        teamTodoDtoList.add(teamTodoDto1);

        TeamTodoDto teamTodoDto2 = new TeamTodoDto();
        teamTodoDto2.setId(UUID.randomUUID().toString());
        teamTodoDto2.setTeamId(teamId);
        teamTodoDto2.setTitle("팀 투두 2");
        teamTodoDto2.setContent("팀 투두 내용 2");
        teamTodoDto2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        teamTodoDto2.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        teamTodoDtoList.add(teamTodoDto2);

        // 서비스 응답 DTO 생성
        TeamTodoListResponseDto responseDto = new TeamTodoListResponseDto();
        responseDto.setTeamTodoDtoList(teamTodoDtoList);

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("팀 투두 리스트 조회 성공")
                .data(responseDto)
                .build();

        when(teamTodoService.getTeamTodoList(eq(teamId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/list", teamId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀 투두 리스트 조회 성공"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.teamTodoDtoList").isArray())
                .andExpect(jsonPath("$.data.teamTodoDtoList.length()").value(2))
                .andExpect(jsonPath("$.data.teamTodoDtoList[0].title").value("팀 투두 1"))
                .andExpect(jsonPath("$.data.teamTodoDtoList[1].title").value("팀 투두 2"));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoList(eq(teamId));
    }

    @Test
    @DisplayName("팀 투두 상세 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetTeamTodoByIdSuccess() throws Exception {
        // Given
        TeamTodoResponseDto responseDto = teamTodoResponseDto;

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("팀 투두 상세 조회 성공")
                .data(responseDto)
                .build();

        when(teamTodoService.getTeamTodoById(eq(todoId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/{teamTodoId}", teamId, todoId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀 투두 상세 조회 성공"))
                .andExpect(jsonPath("$.data").exists());

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoById(eq(todoId));
    }

    @Test
    @DisplayName("나의 팀 투두 전체 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetMyTeamListTodoListSuccess() throws Exception {
        // Given
        MyTeamListResponseDto responseList = myTeamListResponseDto;

        ApiResponse<MyTeamListResponseDto> successResponse = ApiResponse.<MyTeamListResponseDto>builder()
                .result(Result.SUCCESS)
                .message("나의 팀 투두 전체 조회 성공")
                .data(responseList)
                .build();

        when(myTeamTodoService.getMyTeamListAndTodoList(eq("test-user-id")))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/my/list", teamId)  // URL 경로 수정: teamId가 URL에 없어 보임
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("나의 팀 투두 전체 조회 성공"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.teams").isArray())
                .andExpect(jsonPath("$.data.teams.length()").value(1))
                .andExpect(jsonPath("$.data.teams[0].teamId").value(teamId))
                .andExpect(jsonPath("$.data.teams[0].todos").isArray())
                .andExpect(jsonPath("$.data.teams[0].todos.length()").value(1))
                .andExpect(jsonPath("$.data.teams[0].todos[0].todoId").value(todoId))
                .andExpect(jsonPath("$.data.teams[0].todos[0].title").value("테스트 할 일"))
                .andExpect(jsonPath("$.data.teams[0].todos[0].content").value("테스트 내용"))
                .andExpect(jsonPath("$.data.teams[0].todos[0].completed").value(false));

        // 서비스 메서드 호출 검증
        verify(myTeamTodoService, times(1)).getMyTeamListAndTodoList(eq("test-user-id"));
    }

    @Test
    @DisplayName("나의 팀 투두 상세 조회 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testGetMyTeamTodoDetailSuccess() throws Exception {
        // Given
        MyTeamTodoDetailResponseDto responseDto = myTeamTodoDetailResponseDto;

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("나의 팀 투두 상세 조회 성공")
                .data(responseDto)
                .build();

        when(myTeamTodoService.getMyTeamTodoDetail(eq(teamId), eq(teamTodoUserId), eq("test-user-id")))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/my/{teamTodoUserId}", teamId, teamTodoUserId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("나의 팀 투두 상세 조회 성공"))
                .andExpect(jsonPath("$.data").exists());

        // 서비스 메서드 호출 검증
        verify(myTeamTodoService, times(1)).getMyTeamTodoDetail(eq(teamId), eq(teamTodoUserId), eq("test-user-id"));
    }

    @Test
    @DisplayName("팀 투두 수정 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testUpdateTeamTodoSuccess() throws Exception {
        // Given
        TeamTodoRequestDto requestDto = teamTodoRequestDto;
        // requestDto 필드 세팅

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("팀 투두 수정 성공")
                .build();

        when(teamTodoService.updateTeamTodo(any(TeamTodoRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(put("/api/team/{teamId}/todo/{todoId}", teamId, todoId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀 투두 수정 성공"));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).updateTeamTodo(any(TeamTodoRequestDto.class));
    }

    @Test
    @DisplayName("나의 팀 투두 상태 변경 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testUpdateMyTeamTodoSuccess() throws Exception {
        // Given
        MyTeamTodoRequestDto requestDto = myTeamTodoRequestDto;
        // requestDto 필드 세팅

        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("나의 팀 투두 상태 변경 성공")
                .build();

        when(myTeamTodoService.updateMyTeamTodo(any(MyTeamTodoRequestDto.class)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(put("/api/team/{teamId}/todo/my/{teamTodoUserId}", teamId, teamTodoUserId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("나의 팀 투두 상태 변경 성공"));

        // 서비스 메서드 호출 검증
        verify(myTeamTodoService, times(1)).updateMyTeamTodo(any(MyTeamTodoRequestDto.class));
    }

    @Test
    @DisplayName("팀 투두 삭제 성공 테스트")
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void testDeleteTeamTodoSuccess() throws Exception {
        // Given
        ApiResponse successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("팀 투두 삭제 성공")
                .build();

        when(teamTodoService.deleteTeamTodo(eq(todoId)))
                .thenReturn(successResponse);

        // When & Then
        mockMvc.perform(delete("/api/team/{teamId}/todo/{todoId}", teamId, todoId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀 투두 삭제 성공"));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).deleteTeamTodo(eq(todoId));
    }

    @Test
    @DisplayName("인증되지 않은 사용자 접근 테스트")
    void testUnauthenticatedAccess() throws Exception {
        // When & Then - 인증 없이 접근
        mockMvc.perform(get("/api/team/{teamId}/todo/list", teamId)
                        .with(csrf()))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized 응답 기대

        // 서비스 메서드 호출되지 않음 검증
        verify(teamTodoService, never()).getTeamTodoList(any());
    }
}
