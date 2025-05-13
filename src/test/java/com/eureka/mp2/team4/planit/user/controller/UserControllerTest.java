package com.eureka.mp2.team4.planit.user.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private PlanitUserDetails planitUserDetails;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        planitUserDetails = mock(PlanitUserDetails.class);
        when(planitUserDetails.getUsername()).thenReturn("test-user-id");

        userResponseDto = UserResponseDto.builder()
                .email("test@planit.com")
                .createdAt(null)
                .isActive(true)
                .phoneNumber("01012345678")
                .nickname("닉네임")
                .userName("테스트유저")
                .updatedAt(null)
                .build();
    }

    @Test
    @DisplayName("GET /api/users/me - 유저 정보 조회 성공")
    void getMyPageSuccess() {
        when(userService.getMyPageData("test-user-id")).thenReturn(userResponseDto);

        ResponseEntity<ApiResponse<?>> response = userController.getMyPage(planitUserDetails);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getResult()).isEqualTo(Result.SUCCESS);
        assertThat(((UserResponseDto) response.getBody().getData()).getEmail()).isEqualTo("test@planit.com");

        verify(userService, times(1)).getMyPageData("test-user-id");
    }

    @Test
    @DisplayName("PATCH /api/users/me - 닉네임 변경 성공")
    void updateNickNameSuccess() {
        // given
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto("newNickname");

        ApiResponse<?> mockResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("닉네임 변경에 성공했습니다.")
                .build();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updateUser("test-user-id", requestDto)).thenReturn(mockResponse);

        // when
        ResponseEntity<ApiResponse<?>> response = userController.updateUser(planitUserDetails, requestDto, bindingResult);

        // then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getResult()).isEqualTo(Result.SUCCESS);
        assertThat(response.getBody().getMessage()).isEqualTo("닉네임 변경에 성공했습니다.");

        verify(userService, times(1)).updateUser("test-user-id", requestDto);
    }

    @Test
    @DisplayName("PATCH /api/users/me - 닉네임 유효성 검사 실패")
    void updateNickName_validationFail() {
        // given
        UpdateUserRequestDto requestDto = new UpdateUserRequestDto(""); // 빈 닉네임 등 유효하지 않은 값

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("updateNickNameRequestDto", "newNickName", "닉네임은 필수입니다."));

        // when & then
        InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                userController.updateUser(planitUserDetails, requestDto, bindingResult)
        );

        assertThat(exception.getMessage()).isEqualTo("닉네임은 필수입니다.");
        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_success() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPass");

        ApiResponse<?> mockResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("비밀번호 변경에 성공했습니다.")
                .build();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.updatePassword("test-user-id", dto)).thenReturn(mockResponse);

        // when
        ResponseEntity<ApiResponse<?>> response = userController.updatePassword(planitUserDetails, dto, bindingResult);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Result.SUCCESS, response.getBody().getResult());
        assertEquals("비밀번호 변경에 성공했습니다.", response.getBody().getMessage());

        verify(userService).updatePassword("test-user-id", dto);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 유효성 검증 실패")
    void updatePassword_validationFail() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPass");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError()).thenReturn(
                new FieldError("updatePasswordRequestDto", "currentPassword", "현재 비밀번호를 입력해주세요")
        );

        // when & then
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            userController.updatePassword(planitUserDetails, dto, bindingResult);
        });

        assertEquals("현재 비밀번호를 입력해주세요", exception.getMessage());
        verify(userService, never()).updatePassword(any(), any());
    }

}