package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.MyPageResponseDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserSearchResponseDto;

public interface UserService {
    MyPageResponseDto getMyPageData(String userId);

    ApiResponse updateUser(String userId, UpdateUserRequestDto requestDto);

    ApiResponse updatePassword(String userId, UpdatePasswordRequestDto requestDto);

    ApiResponse deleteUser(String userId);

    ApiResponse getUserInfo(String username, String value, String teamId);
}
