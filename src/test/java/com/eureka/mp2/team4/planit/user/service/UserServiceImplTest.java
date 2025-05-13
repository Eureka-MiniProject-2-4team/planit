package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.eureka.mp2.team4.planit.user.constants.Messages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final String userId = "test-user-id";
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "password",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );
    }

    @Test
    @DisplayName("성공 - 유저 정보 조회")
    void getMyPageDataSuccess() {
        when(userMapper.findById(userId)).thenReturn(userDto);

        UserResponseDto result = userService.getMyPageData(userId);

        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
        verify(userMapper, times(1)).findById(userId);
    }

    @Test
    @DisplayName("실패 - 유저 정보 없음")
    void getMyPageDataUserNotFound() {
        when(userMapper.findById(userId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getMyPageData(userId));

        assertThat(exception.getMessage()).isEqualTo(NOT_FOUND_USER);
    }

    @Test
    @DisplayName("실패 - 내부 서버 에러 발생")
    void getMyPageDataInternalServerError() {
        when(userMapper.findById(userId)).thenThrow(RuntimeException.class);

        assertThrows(InternalServerErrorException.class,
                () -> userService.getMyPageData(userId));
    }

    @Test
    @DisplayName("닉네임 수정 성공 테스트")
    void updateUser_success() {
        // given
        String userId = "user-123";
        UpdateUserRequestDto dto = new UpdateUserRequestDto("newNickname");

        // when
        ApiResponse response = userService.updateUser(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_NICKNAME_SUCCESS, response.getMessage());
        verify(userMapper, times(1)).updateNickName(userId, "newNickname");
    }

    @Test
    @DisplayName("닉네임 수정 실패 테스트 - DataAccessException 발생")
    void updateUser_dataAccessException() {
        // given
        String userId = "user-123";
        UpdateUserRequestDto dto = new UpdateUserRequestDto("newNickname");

        doThrow(new DataAccessException(UPDATE_NICKNAME_FAIL) {
        }).when(userMapper).updateNickName(userId, "newNickname");

        // when & then
        assertThrows(DatabaseException.class, () -> userService.updateUser(userId, dto));
        verify(userMapper, times(1)).updateNickName(userId, "newNickname");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_success() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        UserDto user = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "encodedOldPass",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );

        when(userMapper.findById(userId)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // when
        ApiResponse<?> response = userService.updatePassword(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_PASSWORD_SUCCESS, response.getMessage());
        verify(userMapper).updatePassword(userId, "encodedPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 사용자 없음")
    void updatePassword_userNotFound() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        when(userMapper.findById(userId)).thenReturn(null);

        // when & then
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.updatePassword(userId, dto);
        });

        assertEquals(NOT_FOUND_USER, ex.getMessage());
        verify(userMapper, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - DB 예외 발생")
    void updatePassword_dbException() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        UserDto user = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "encodedOldPass",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );

        when(userMapper.findById(userId)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        doThrow(new DataAccessException("DB 오류") {
        }).when(userMapper).updatePassword(userId, "encodedPassword");

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userService.updatePassword(userId, dto);
        });

        assertEquals(UPDATE_PASSWORD_FAIL, ex.getMessage());
    }
}