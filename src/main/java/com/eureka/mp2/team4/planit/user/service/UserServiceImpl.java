package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.service.UserTeamQueryService;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.eureka.mp2.team4.planit.user.constants.Messages.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserTeamQueryService userTeamQueryService;

    @Override
    public UserResponseDto getMyPageData(String userId) {
        try {
            UserDto userDto = userMapper.findById(userId);
            if (userDto == null) {
                throw new NotFoundException(NOT_FOUND_USER);
            }
            return UserResponseDto.builder()
                    .email(userDto.getEmail())
                    .phoneNumber(userDto.getPhoneNumber())
                    .nickname(userDto.getNickName())
                    .userName(userDto.getUserName())
                    .build();
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

    }

    @Override
    public ApiResponse updateUser(String userId, UpdateUserRequestDto requestDto) {
        if (requestDto.getNewNickName() != null) {
            return updateNickName(userId, requestDto);
        }

        return updateIsActive(userId, requestDto);

    }

    private ApiResponse updateIsActive(String userId, UpdateUserRequestDto requestDto) {
        try {
            userMapper.updateIsActive(userId, requestDto.getIsActive());
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_DISACTIVE)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException(UPDATE_DISACTIVE_FAIL);
        }
    }

    private ApiResponse<Object> updateNickName(String userId, UpdateUserRequestDto requestDto) {
        try {
            userMapper.updateNickName(userId, requestDto.getNewNickName());
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_NICKNAME_SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException(UPDATE_NICKNAME_FAIL);
        }
    }

    @Override
    @Transactional
    public ApiResponse updatePassword(String userId, UpdatePasswordRequestDto requestDto) {
        try {
            UserDto userDto = userMapper.findById(userId);
            if (userDto == null) {
                throw new NotFoundException(NOT_FOUND_USER);
            }
            String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
            userMapper.updatePassword(userId, encodedPassword);

            return ApiResponse.builder()
                    .message(UPDATE_PASSWORD_SUCCESS)
                    .result(Result.SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException(UPDATE_PASSWORD_FAIL);
        }
    }

    @Override
    public ApiResponse deleteUser(String userId) {
        if (userTeamQueryService.isUserTeamLeader(userId)) {
            return ApiResponse.builder()
                    .message(LEADER_CAN_NOT_DELETE)
                    .result(Result.FAIL)
                    .build();
        }

        try {
            userMapper.deleteById(userId);
            return ApiResponse.builder()
                    .message(DELETE_USER_SUCCESS)
                    .result(Result.SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException(DELETE_USER_FAIL);
        }
    }
}
