package com.eureka.mp2.team4.planit.todo.team.controller;

import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.team.service.TeamTodoService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;

@ExtendWith(MockitoExtension.class)
public class TeamTodoControllerTest {

    @Mock
    private TeamTodoService teamTodoService;

    @InjectMocks
    private TeamTodoController teamTodoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String teamId;
    private String todoId;
    private TeamTodoRequestDto teamTodoRequestDto;
    private TeamTodoDto teamTodoDto;
    private TeamTodoResponseDto teamTodoResponseDto;
    private TeamTodoListResponseDto teamTodoListResponseDto;
    private Timestamp currentTime;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(teamTodoController).build();
        objectMapper = new ObjectMapper();

        teamId = "team-123";
        todoId = "todo-123";
        currentTime = Timestamp.from(Instant.now());

        // 요청 DTO 세팅
        teamTodoRequestDto = new TeamTodoRequestDto();
        teamTodoRequestDto.setTeamId(teamId);
        teamTodoRequestDto.setTitle("팀 미팅 준비하기");
        teamTodoRequestDto.setContent("회의록 및 발표자료 준비");
        teamTodoRequestDto.setCompleted(false);

        // 응답용 DTO 세팅
        teamTodoDto = new TeamTodoDto();
        teamTodoDto.setId(todoId);
        teamTodoDto.setTeamId(teamId);
        teamTodoDto.setTitle("팀 미팅 준비하기");
        teamTodoDto.setContent("회의록 및 발표자료 준비");
        teamTodoDto.setCompleted(false);
        teamTodoDto.setCreatedAt(currentTime);
        teamTodoDto.setUpdatedAt(currentTime);

        // 상세 조회 응답 DTO 세팅
        teamTodoResponseDto = new TeamTodoResponseDto();
        teamTodoResponseDto.setTitle("팀 미팅 준비하기");
        teamTodoResponseDto.setContent("회의록 및 발표자료 준비");
        teamTodoResponseDto.setCompleted(false);
        teamTodoResponseDto.setCreatedAt(currentTime);
        teamTodoResponseDto.setUpdatedAt(currentTime);

        // 목록 조회 응답 DTO 세팅
        List<TeamTodoDto> todoList = Arrays.asList(
                teamTodoDto,
                createAnotherTeamTodoDto()
        );

        teamTodoListResponseDto = new TeamTodoListResponseDto();
        teamTodoListResponseDto.setTeamTodoDtoList(todoList);
    }

    // 추가 테스트용 DTO 생성 메서드
    private TeamTodoDto createAnotherTeamTodoDto() {
        TeamTodoDto anotherDto = new TeamTodoDto();
        anotherDto.setId("todo-456");
        anotherDto.setTeamId(teamId);
        anotherDto.setTitle("두 번째 할일");
        anotherDto.setContent("두 번째 내용");
        anotherDto.setCompleted(false);
        anotherDto.setCreatedAt(currentTime);
        anotherDto.setUpdatedAt(currentTime);
        return anotherDto;
    }

    // CREATE
    @Test
    @DisplayName("팀 투두 생성 성공 테스트")
    void testCreateTeamTodoSuccess() throws Exception {
        // Given
        when(teamTodoService.createTeamTodo(any(TeamTodoRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(REGISTER_TEAMTODO_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team/{teamId}/todo",teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamTodoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAMTODO_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).createTeamTodo(any(TeamTodoRequestDto.class));
    }

    @Test
    @DisplayName("팀 투두 생성 실패 테스트")
    void testCreateTeamTodoFail() throws Exception {
        // Given
        when(teamTodoService.createTeamTodo(any(TeamTodoRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(REGISTER_TEAMTODO_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/team/{teamId}/todo",teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamTodoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(REGISTER_TEAMTODO_FAIL));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).createTeamTodo(any(TeamTodoRequestDto.class));
    }

    // READ - List
    @Test
    @DisplayName("팀 투두 리스트 조회 성공 테스트")
    void testGetTeamTodoListSuccess() throws Exception {
        // Given
        when(teamTodoService.getTeamTodoList(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(GET_TEAMTODO_LIST_SUCCESS)
                        .data(teamTodoListResponseDto)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/list",teamId)
                        .param("teamId", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_TEAMTODO_LIST_SUCCESS))
                .andExpect(jsonPath("$.data.teamTodoDtoList").isArray())
                .andExpect(jsonPath("$.data.teamTodoDtoList.length()").value(2))
                .andExpect(jsonPath("$.data.teamTodoDtoList[0].id").value(todoId))
                .andExpect(jsonPath("$.data.teamTodoDtoList[1].id").value("todo-456"));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoList(eq(teamId));
    }

    @Test
    @DisplayName("팀 투두 리스트 조회 실패 테스트")
    void testGetTeamTodoListFail() throws Exception {
        // Given
        when(teamTodoService.getTeamTodoList(eq(teamId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(GET_TEAMTODO_LIST_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/{teamId}/todo/list",teamId)
                        .param("teamId", teamId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(GET_TEAMTODO_LIST_FAIL))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoList(eq(teamId));
    }

    // READ - Detail
    @Test
    @DisplayName("팀 투두 상세 조회 성공 테스트")
    void testGetTeamTodoByIdSuccess() throws Exception {
        // Given
        when(teamTodoService.getTeamTodoById(eq(todoId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(GET_TEAMTODO_SUCCESS)
                        .data(teamTodoResponseDto)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/todo/{teamTodoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(GET_TEAMTODO_SUCCESS))
                .andExpect(jsonPath("$.data.title").value("팀 미팅 준비하기"))
                .andExpect(jsonPath("$.data.content").value("회의록 및 발표자료 준비"))
                .andExpect(jsonPath("$.data.completed").value(false));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoById(eq(todoId));
    }

    @Test
    @DisplayName("팀 투두 상세 조회 실패 테스트")
    void testGetTeamTodoByIdFail() throws Exception {
        // Given
        when(teamTodoService.getTeamTodoById(eq(todoId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(GET_TEAMTODO_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(get("/api/team/todo/{teamTodoId}", todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(GET_TEAMTODO_FAIL))
                .andExpect(jsonPath("$.data").doesNotExist());

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).getTeamTodoById(eq(todoId));
    }

    // UPDATE
    @Test
    @DisplayName("팀 투두 수정 성공 테스트")
    void testUpdateTeamTodoSuccess() throws Exception {
        // Given
        TeamTodoRequestDto updateRequest = new TeamTodoRequestDto();
        updateRequest.setTeamId(teamId);
        updateRequest.setTitle("수정된 할일");
        updateRequest.setContent("수정된 내용");
        updateRequest.setCompleted(true);

        when(teamTodoService.updateTeamTodo(any(TeamTodoRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(UPDATE_TEAMTODO_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{teamId}/todo/{teamTodoId}", teamId, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(UPDATE_TEAMTODO_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).updateTeamTodo(any(TeamTodoRequestDto.class));
    }

    @Test
    @DisplayName("팀 투두 수정 실패 테스트")
    void testUpdateTeamTodoFail() throws Exception {
        // Given
        TeamTodoRequestDto updateRequest = new TeamTodoRequestDto();
        updateRequest.setTeamId(teamId);
        updateRequest.setTitle("수정된 할일");
        updateRequest.setContent("수정된 내용");
        updateRequest.setCompleted(true);

        when(teamTodoService.updateTeamTodo(any(TeamTodoRequestDto.class))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(UPDATE_TEAMTODO_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(put("/api/team/{teamId}/todo/{teamTodoId}", teamId, todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(UPDATE_TEAMTODO_FAIL));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).updateTeamTodo(any(TeamTodoRequestDto.class));
    }

    // DELETE
    @Test
    @DisplayName("팀 투두 삭제 성공 테스트")
    void testDeleteTeamTodoSuccess() throws Exception {
        // Given
        when(teamTodoService.deleteTeamTodo(eq(todoId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message(DELETE_TEAMTODO_SUCCESS)
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{teamId}/todo/{teamTodoId}", teamId, todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAMTODO_SUCCESS));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).deleteTeamTodo(eq(todoId));
    }

    @Test
    @DisplayName("팀 투두 삭제 실패 테스트")
    void testDeleteTeamTodoFail() throws Exception {
        // Given
        when(teamTodoService.deleteTeamTodo(eq(todoId))).thenReturn(
                ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(DELETE_TEAMTODO_FAIL)
                        .build()
        );

        // When & Then
        mockMvc.perform(delete("/api/team/{teamId}/todo/{teamTodoId}", teamId, todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value(DELETE_TEAMTODO_FAIL));

        // 서비스 메서드 호출 검증
        verify(teamTodoService, times(1)).deleteTeamTodo(eq(todoId));
    }
}
