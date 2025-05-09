package com.eureka.mp2.team4.planit.todo.personal.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.service.PersonalTodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(PersonalTodoController.class)
class PersonalTodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonalTodoService personalTodoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createTest() throws Exception {
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .userId("user-1")
                .title("title")
                .content("content")
                .targetDate(LocalDateTime.now())
                .build();

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 생성 완료")
                .build();

        Mockito.when(personalTodoService.create(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/todo/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 생성 완료"));
    }

    @Test
    @WithMockUser
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

        Mockito.when(personalTodoService.update(Mockito.eq(id), Mockito.any())).thenReturn(response);

        mockMvc.perform(patch("/api/todo/personal/" + id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 수정 완료"));
    }

    @Test
    @WithMockUser
    void deleteTest() throws Exception {
        String id = "todo-1";

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 삭제 완료")
                .build();

        Mockito.when(personalTodoService.delete(id)).thenReturn(response);

        mockMvc.perform(delete("/api/todo/personal/" + id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 삭제 완료"));
    }

    @Test
    @WithMockUser
    void getByIdTest() throws Exception {
        String id = "todo-1";

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 조회 완료")
                .data(null)
                .build();

        Mockito.when(personalTodoService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/api/todo/personal/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 조회 완료"));
    }

    @Test
    @WithMockUser
    void getAllByUserTest() throws Exception {
        String userId = "user-1";

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("개인 투두 전체 조회 완료")
                .data(null)
                .build();

        Mockito.when(personalTodoService.getAllByUser(userId)).thenReturn(response);

        mockMvc.perform(get("/api/todo/personal")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("개인 투두 전체 조회 완료"));
    }
}
