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
            UserResponseDto userDto = userMapper.findMyPageData(userId);
            if (userDto == null) {
                throw new NotFoundException(NOT_FOUND_USER);
            }
            return userDto;
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
            UserDto userDto = userMapper.findUserById(userId);
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
            userMapper.deleteUserById(userId);
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
            throw new NotFoundException(NOT_FOUND_USER);
        }

        if (targetUser.getId().equals(currentUserId)) {
            return buildSelfResponse(targetUser);
        }

        FriendStatus friendStatus = null;
        String teamMembershipStatus = null;

        if (teamId == null) {
            friendStatus = friendQueryService.areFriends(currentUserId, targetUser.getId());
        } else {
            teamMembershipStatus = userTeamQueryService.getTeamMemberShipStatus(teamId, targetUser.getId());
        }

        UserSearchResponseDto responseDto = buildUserSearchResponse(targetUser, friendStatus, teamMembershipStatus);
        return ApiResponse.builder()
                .message(FOUND_USER_SUCCESS)
                .data(responseDto)
                .result(Result.SUCCESS)
                .build();
    }

    private ApiResponse buildSelfResponse(UserDto user) {
        UserSearchResponseDto dto = UserSearchResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .isMe(true)
                .build();
        return ApiResponse.builder()
                .data(dto)
                .message(FOUND_USER_SUCCESS)
                .result(Result.SUCCESS)
                .build();
    }

    private UserSearchResponseDto buildUserSearchResponse(UserDto user, FriendStatus friendStatus, String teamStatus) {
        return UserSearchResponseDto.builder()
                .id(user.getId())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .friendStatus(friendStatus)
                .teamMembershipStatus(teamStatus)
                .build();
    }

    private UserDto findUserByValue(String value) {
        try {
            if (value.contains("@")) {
                return userMapper.findActiveUserByEmail(value);
            } else {
                return userMapper.findActiveUserByNickName(value);
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(FOUND_USER_FAIL);
        }
    }

}
