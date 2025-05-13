package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.service.FriendQueryService;
import com.eureka.mp2.team4.planit.team.service.UserTeamQueryService;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.UserSearchResponseDto;
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
    private final FriendQueryService friendQueryService;

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

    @Override
    public ApiResponse getUserInfo(String currentUserId, String value, String teamId) {
        UserDto targetUser = findUserByValue(value);
        if (targetUser == null) {
            return ApiResponse.builder()
                    .message(NOT_FOUND_USER)
                    .result(Result.FAIL)
                    .build();
        }

        FriendStatus friendStatus = null;
        String teamMembershipStatus = null;

        if (teamId == null) {
            // 팀 정보 없으면 → 친구 관계 확인
            friendStatus = friendQueryService.areFriends(currentUserId, targetUser.getId());
        } else {
            // 팀 정보 있으면 → 팀 소속 여부 확인
            teamMembershipStatus = userTeamQueryService.getTeamMemberShipStatus(teamId, targetUser.getId());
        }
        UserSearchResponseDto responseDto = UserSearchResponseDto.builder()
                .id(targetUser.getId())
                .nickName(targetUser.getNickName())
                .email(targetUser.getEmail())
                .friendStatus(friendStatus)
                .teamMembershipStatus(teamMembershipStatus)
                .build();

        return ApiResponse.builder()
                .result(Result.SUCCESS)
                .message(FOUND_USER_SUCCESS)
                .data(responseDto)
                .build();
    }

    private UserDto findUserByValue(String value) {
        try {
            if (value.contains("@")) {
                return userMapper.findByEmail(value);
            } else {
                return userMapper.findByNickName(value);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(FOUND_USER_FAIL);
        }
    }

}
