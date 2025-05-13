package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.auth.dto.request.FindEmailRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.VerifyPasswordRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.response.FindEmailResponseDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.DuplicateFieldException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.*;
import static com.eureka.mp2.team4.planit.user.constants.Messages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRegisterRequestDto validDto;

    @BeforeEach
    void setUpDto() {
        validDto = UserRegisterRequestDto.builder()
                .email("test@planit.com")
                .userName("testuser")
                .password("secure1234!")
                .nickName("nickname")
                .phoneNumber("01012345678")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void register_success() {
        when(userMapper.isExistEmail(anyString())).thenReturn(false);
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(false);
        when(userMapper.isExistNickName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        ApiResponse response = authService.register(validDto);

        assertThat(response.getResult()).isEqualTo(Result.SUCCESS);
        verify(userMapper, times(1)).register(any());
    }

    @Test
    void register_fail_duplicatePhone() {
        when(userMapper.isExistEmail(anyString())).thenReturn(false);
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(true);

        assertThrows(DuplicateFieldException.class, () -> authService.register(validDto));
    }

    @Test
    void register_fail_duplicateNickName() {
        when(userMapper.isExistEmail(anyString())).thenReturn(false);
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(false);
        when(userMapper.isExistNickName(anyString())).thenReturn(true);

        assertThrows(DuplicateFieldException.class, () -> authService.register(validDto));
    }

    @Test
    void checkEmailIsExist_duplicate() {
        when(userMapper.isExistEmail(anyString())).thenReturn(true);
        ApiResponse res = authService.checkEmailIsExist("test@planit.com");

        assertThat(res.getResult()).isEqualTo(Result.DUPLICATED);
    }

    @Test
    void checkEmailIsExist_valid() {
        when(userMapper.isExistEmail(anyString())).thenReturn(false);
        ApiResponse res = authService.checkEmailIsExist("test@planit.com");

        assertThat(res.getResult()).isEqualTo(Result.SUCCESS);
    }

    @Test
    void checkPhoneNumberIsExist_valid() {
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(false);
        ApiResponse res = authService.checkPhoneNumberIsExist("01012345678");

        assertThat(res.getResult()).isEqualTo(Result.SUCCESS);
    }

    @Test
    void checkPhoneNumberIsExist_duplicate() {
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(true);
        ApiResponse res = authService.checkPhoneNumberIsExist("01012345678");

        assertThat(res.getResult()).isEqualTo(Result.DUPLICATED);
    }

    @Test
    void checkNickNameIsExist_duplicate() {
        when(userMapper.isExistNickName(anyString())).thenReturn(true);
        ApiResponse res = authService.checkNickNameIsExist("nickname");

        assertThat(res.getResult()).isEqualTo(Result.DUPLICATED);
    }

    @Test
    void checkNickNameIsExist_valid() {
        when(userMapper.isExistNickName(anyString())).thenReturn(false);
        ApiResponse res = authService.checkNickNameIsExist("nickname");

        assertThat(res.getResult()).isEqualTo(Result.SUCCESS);
    }

    @Test
    void register_fail_databaseException_onRegister() {
        when(userMapper.isExistEmail(anyString())).thenReturn(false);
        when(userMapper.isExistPhoneNumber(anyString())).thenReturn(false);
        when(userMapper.isExistNickName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        doThrow(new DataAccessException("DB 오류") {
        }).when(userMapper).register(any());

        assertThrows(DatabaseException.class, () -> authService.register(validDto));
    }

    @Test
    void checkDuplicateEmail_databaseException() {
        when(userMapper.isExistEmail(anyString())).thenThrow(new DataAccessException("DB 오류") {
        });
        assertThrows(DatabaseException.class, () -> authService.checkEmailIsExist("test@planit.com"));
    }

    @Test
    void checkDuplicatePhone_databaseException() {
        when(userMapper.isExistPhoneNumber(anyString())).thenThrow(new DataAccessException("DB 오류") {
        });
        assertThrows(DatabaseException.class, () -> authService.checkPhoneNumberIsExist("01012345678"));
    }

    @Test
    void checkDuplicateNick_databaseException() {
        when(userMapper.isExistNickName(anyString())).thenThrow(new DataAccessException("DB 오류") {
        });
        assertThrows(DatabaseException.class, () -> authService.checkNickNameIsExist("nickname"));
    }

    private final String userId = "test-user-id";

    private final UserDto mockUser = new UserDto(
            userId,
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

    @Test
    @DisplayName("비밀번호 검증 성공")
    void verifyPassword_success() {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("password123");

        when(userMapper.findById(userId)).thenReturn(mockUser);
        when(passwordEncoder.matches("password123", "encodedOldPass")).thenReturn(true);

        // when
        ApiResponse<?> response = authService.verifyPassword(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(VERIFY_PASSWORD_SUCCESS, response.getMessage());
    }

    @Test
    @DisplayName("비밀번호 불일치 - FAIL 반환")
    void verifyPassword_fail_mismatch() {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("wrong-password");

        when(userMapper.findById(userId)).thenReturn(mockUser);
        when(passwordEncoder.matches("wrong-password", "encodedOldPass")).thenReturn(false);

        // when
        ApiResponse<?> response = authService.verifyPassword(userId, dto);

        // then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(NOT_MATCH_CURRENT_PASSWORD, response.getMessage());
    }

    @Test
    @DisplayName("사용자 없음 - NotFoundException 발생")
    void verifyPassword_notFoundUser() {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("password");
        when(userMapper.findById(userId)).thenReturn(null);

        // when & then
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            authService.verifyPassword(userId, dto);
        });

        assertEquals(NOT_FOUND_USER, ex.getMessage());
    }

    @Test
    @DisplayName("DB 오류 발생 - DatabaseException 발생")
    void verifyPassword_dataAccessException() {
        // given
        VerifyPasswordRequestDto dto = new VerifyPasswordRequestDto("password");
        when(userMapper.findById(userId)).thenThrow(new DataAccessException("DB 오류") {
        });

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            authService.verifyPassword(userId, dto);
        });

        assertEquals(VERIFY_PASSWORD_FAIL, ex.getMessage());
    }

    @Test
    @DisplayName("이메일 찾기 성공")
    void findEmail_success() {
        // given
        FindEmailRequestDto request = new FindEmailRequestDto("홍길동", "01012345678");
        UserDto mockUser = new UserDto("id", "hong@test.com", "홍길동", "encodedPw", "nick", UserRole.ROLE_USER, null, null, true, "01012345678");

        when(userMapper.findUserByNameAndPhoneNumber("홍길동", "01012345678")).thenReturn(mockUser);

        // when
        ApiResponse<?> response = authService.findEmail(request);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FOUND_EMAIL_SUCCESS, response.getMessage());

        FindEmailResponseDto dto = (FindEmailResponseDto) response.getData();
        assertEquals("hong@test.com", dto.getEmail());

        verify(userMapper).findUserByNameAndPhoneNumber("홍길동", "01012345678");
    }

    @Test
    @DisplayName("이메일 찾기 실패 - 유저 없음")
    void findEmail_userNotFound() {
        // given
        FindEmailRequestDto request = new FindEmailRequestDto("홍길순", "01000000000");

        when(userMapper.findUserByNameAndPhoneNumber("홍길순", "01000000000")).thenReturn(null);

        // when
        ApiResponse<?> response = authService.findEmail(request);

        // then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(NOT_FOUND_USER, response.getMessage());

        verify(userMapper).findUserByNameAndPhoneNumber("홍길순", "01000000000");
    }

    @Test
    @DisplayName("이메일 찾기 실패 - DB 오류")
    void findEmail_dbError() {
        // given
        FindEmailRequestDto request = new FindEmailRequestDto("홍길동", "01012345678");

        when(userMapper.findUserByNameAndPhoneNumber(any(), any()))
                .thenThrow(new DataAccessException("DB 오류") {
                });

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            authService.findEmail(request);
        });

        assertEquals(FOUND_EMAIL_FAIL, ex.getMessage());
        verify(userMapper).findUserByNameAndPhoneNumber("홍길동", "01012345678");
    }
}