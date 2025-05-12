package com.eureka.mp2.team4.planit.user.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {

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
}