package com.eureka.mp2.team4.planit.auth.controller;

import com.eureka.mp2.team4.planit.auth.dto.request.FindEmailRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.VerifyPasswordRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.response.FindEmailResponseDto;
import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.AuthService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.WithMockPlanitUser;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .userName("testuser")
                .password("test1234!")
                .nickName("nickname")
                .phoneNumber("01012345678")
                .build();

        when(authService.register(any())).thenReturn(ApiResponse.builder()
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
                .userName("testuser")
                .password("test1234!")
                .nickName("nickname")
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
        when(authService.checkEmailIsExist(eq("test@planit.com"))).thenReturn(ApiResponse.builder()
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
        when(authService.checkPhoneNumberIsExist(eq("01012345678"))).thenReturn(ApiResponse.builder()
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
        when(authService.checkNickNameIsExist(eq("nickName"))).thenReturn(ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("사용 가능한 닉네임입니다.")
                .build());

        mockMvc.perform(get("/auth/check-nickName")
                        .param("value", "nickName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()));
    }

    private PlanitUserDetails planitUserDetails;

    @InjectMocks
    private AuthController authController;
    @Mock
    private AuthService authService2;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "password123",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );
        planitUserDetails = new PlanitUserDetails(userDto);
    }

    @Test
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    void verifyPassword_success() throws Exception {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("password123");

        ApiResponse<?> mockResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("비밀번호 확인 완료")
                .build();

        given(authService.verifyPassword(eq("test-user-id"), any(VerifyPasswordRequestDto.class)))
                .willReturn(mockResponse);

        mockMvc.perform(post("/auth/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("비밀번호 확인 완료"));
    }

    @Test
    @WithMockPlanitUser(username = "test-user-id", role = "ROLE_USER")
    @DisplayName("POST /auth/verify-password - 비밀번호 검증 실패")
    void verifyPassword_fail() throws Exception {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("wrong-password");

        ApiResponse<?> mockResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message("현재 비밀번호가 일치하지 않습니다.")
                .build();

        given(authService.verifyPassword(eq("test-user-id"), any(VerifyPasswordRequestDto.class)))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/auth/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("FAIL"))
                .andExpect(jsonPath("$.message").value("현재 비밀번호가 일치하지 않습니다."));

        verify(authService, times(1)).verifyPassword(eq("test-user-id"), any(VerifyPasswordRequestDto.class));
    }

    @Test
    @DisplayName("이메일 찾기 성공")
    void findEmail_success() throws Exception {
        // given
        FindEmailRequestDto request = new FindEmailRequestDto("홍길동", "01012345678");

        ApiResponse<?> successResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("이메일 조회에 성공했습니다.")
                .data(FindEmailResponseDto.builder().email("hong@test.com").build())
                .build();

        when(authService.findEmail(any(FindEmailRequestDto.class))).thenReturn(successResponse);

        // when & then
        mockMvc.perform(post("/auth/find-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("이메일 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.email").value("hong@test.com"));
    }

    @Test
    @DisplayName("이메일 찾기 실패 - 유저 없음")
    void findEmail_userNotFound() throws Exception {
        // given
        FindEmailRequestDto request = new FindEmailRequestDto("홍길순", "01099999999");

        ApiResponse<?> failResponse = ApiResponse.builder()
                .result(Result.FAIL)
                .message("유저를 찾을 수 없습니다.")
                .build();

        when(authService.findEmail(any(FindEmailRequestDto.class))).thenReturn(failResponse);

        // when & then
        mockMvc.perform(post("/auth/find-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(Result.FAIL.name()))
                .andExpect(jsonPath("$.message").value("유저를 찾을 수 없습니다."));
    }
}
