package com.eureka.mp2.team4.planit.todo.personal.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.service.PersonalTodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonalTodoController.class)
class PersonalTodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonalTodoService personalTodoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private PlanitUserDetails userDetails;

    private static final String USER_ID = "user-1";

    @BeforeEach
    void setUp() {
        Mockito.when(userDetails.getUsername()).thenReturn(USER_ID);
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(userDetails, null);
        auth.setAuthenticated(true); // 인증된 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createTest() throws Exception {
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .title("title")
                .content("content")
                .targetDate(LocalDateTime.now())
                .build();

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 생성 완료")
                .build();

        Mockito.when(personalTodoService.create(Mockito.eq(USER_ID), Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/todo/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 생성 완료"));
    }

    @Test
    void updateTest() throws Exception {
        String id = "todo-1";
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .title("updated")
                .content("updated")
                .targetDate(LocalDateTime.now())
                .isCompleted(true)
                .build();

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 수정 완료")
                .build();

        Mockito.when(personalTodoService.update(Mockito.eq(USER_ID), Mockito.eq(id), Mockito.any())).thenReturn(response);

        mockMvc.perform(patch("/api/todo/me/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 수정 완료"));
    }

    @Test
    void deleteTest() throws Exception {
        String id = "todo-1";

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 삭제 완료")
                .build();

        Mockito.when(personalTodoService.delete(USER_ID, id)).thenReturn(response);

        mockMvc.perform(delete("/api/todo/me/" + id).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 삭제 완료"));
    }

    @Test
    void getByIdTest() throws Exception {
        String id = "todo-1";

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 조회 완료")
                .data(null)
                .build();

        Mockito.when(personalTodoService.getById(USER_ID, id)).thenReturn(response);

        mockMvc.perform(get("/api/todo/me/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 조회 완료"));
    }

    @Test
    void getAllByUserTest() throws Exception {
        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 전체 조회 완료")
                .data(null)
                .build();

        Mockito.when(personalTodoService.getAllByUser(USER_ID)).thenReturn(response);

        mockMvc.perform(get("/api/todo/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 전체 조회 완료"));
    }
}
