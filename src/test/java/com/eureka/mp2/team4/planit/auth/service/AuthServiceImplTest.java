package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.DuplicateFieldException;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
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
}