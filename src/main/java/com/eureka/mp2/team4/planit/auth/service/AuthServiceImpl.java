package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.DuplicateFieldException;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.auth.constants.Messages.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse register(UserRegisterRequestDto requestDto) {
        validateRegisterData(requestDto);
        requestDto.setId(UUID.randomUUID().toString());
        requestDto.setRole(UserRole.USER);
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        saveRegisterData(requestDto);

        return ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(REGISTER_SUCCESS)
                .build();
    }

    @Override
    public ApiResponse checkEmailIsExist(String email) {
        try {
            checkDuplicateEmail(email);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(VALID_EMAIL)
                    .build();
        } catch (DuplicateFieldException e) {
            return ApiResponse.builder()
                    .result(Result.DUPLICATED)
                    .message(DUPLICATED_EMAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse checkPhoneNumberIsExist(String phoneNumber) {
        try {
            checkDuplicatePhoneNumber(phoneNumber);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(VALID_PHONE_NUMBER)
                    .build();
        } catch (DuplicateFieldException e) {
            return ApiResponse.builder()
                    .result(Result.DUPLICATED)
                    .message(DUPLICATED_PHONE_NUMBER)
                    .build();
        }
    }

    @Override
    public ApiResponse checkNickNameIsExist(String nickName) {
        try {
            checkDuplicateNickName(nickName);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(VALID_NICKNAME)
                    .build();
        } catch (DuplicateFieldException e) {
            return ApiResponse.builder()
                    .result(Result.DUPLICATED)
                    .message(DUPLICATED_NICKNAME)
                    .build();
        }
    }

    private void validateRegisterData(UserRegisterRequestDto requestDto) {
        checkDuplicateEmail(requestDto.getEmail());
        checkDuplicatePhoneNumber(requestDto.getPhoneNumber());
        checkDuplicateNickName(requestDto.getNickname());
    }

    private void saveRegisterData(UserRegisterRequestDto requestDto) {
        try {
            userMapper.register(requestDto);
        } catch (DataAccessException e) {
            throw new DatabaseException(REGISTER_FAIL);
        }
    }

    private void checkDuplicateEmail(String email) {
        try {
            if (userMapper.isExistEmail(email)) {
                throw new DuplicateFieldException(DUPLICATED_EMAIL);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(CHECK_EMAIL_FAIL);
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) {
        try {
            if (userMapper.isExistPhoneNumber(phoneNumber)) {
                throw new DuplicateFieldException(DUPLICATED_PHONE_NUMBER);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(CHECK_PHONE_NUMBER_FAIL);
        }
    }

    private void checkDuplicateNickName(String nickName) {
        try {
            if (userMapper.isExistNickName(nickName)) {
                throw new DuplicateFieldException(DUPLICATED_NICKNAME);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(CHECK_NICKNAME_FAIL);
        }
    }
}
