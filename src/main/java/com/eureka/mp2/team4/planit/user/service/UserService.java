package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.user.dto.UpdateNickNameRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto getMyPageData(String userId);

    ApiResponse updateNickName(String userId, UpdateNickNameRequestDto requestDto);
}
