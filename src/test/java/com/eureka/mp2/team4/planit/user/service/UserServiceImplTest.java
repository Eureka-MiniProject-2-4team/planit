package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.eureka.mp2.team4.planit.user.constants.Messages.NOT_FOUND_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

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
}