package com.eureka.mp2.team4.planit.auth.controller;

import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.auth.service.AuthService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        UserRegisterRequestDto dto = UserRegisterRequestDto.builder()
                .email("test@planit.com")
                .username("testuser")
                .password("test1234!")
                .nickname("nickname")
                .phoneNumber("01012345678")
                .build();

        Mockito.when(authService.register(any())).thenReturn(ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("회원가입 성공")
                .build());

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()));
    }

    @Test
    @DisplayName("회원가입 입력값 오류")
    void register_fail() throws Exception {
        UserRegisterRequestDto dto = UserRegisterRequestDto.builder()
                .email("Test@planit.com")
                .username("testuser")
                .password("test1234!")
                .nickname("nickname")
                .phoneNumber("01012345678")
                .build();

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(Result.INVALIDATED.name()));
    }

    @Test
    @DisplayName("이메일 중복 확인 성공")
    void checkEmail_success() throws Exception {
        Mockito.when(authService.checkEmailIsExist(eq("test@planit.com"))).thenReturn(ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("사용 가능한 이메일입니다.")
                .build());

        mockMvc.perform(get("/auth/check-email")
                        .param("value", "test@planit.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()));
    }

    @Test
    @DisplayName("휴대폰 번호 중복 확인 성공")
    void checkPhone_success() throws Exception {
        Mockito.when(authService.checkPhoneNumberIsExist(eq("01012345678"))).thenReturn(ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("사용 가능한 번호입니다.")
                .build());

        mockMvc.perform(get("/auth/check-phoneNumber")
                        .param("value", "01012345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()));
    }

    @Test
    @DisplayName("닉네임 중복 확인 성공")
    void checkNickname_success() throws Exception {
        Mockito.when(authService.checkNickNameIsExist(eq("nickname"))).thenReturn(ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("사용 가능한 닉네임입니다.")
                .build());

        mockMvc.perform(get("/auth/check-nickname")
                        .param("value", "nickname"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()));
    }
}
