package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto getMyPageData(String userId);
}
