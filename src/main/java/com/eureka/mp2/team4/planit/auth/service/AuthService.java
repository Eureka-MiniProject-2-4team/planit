package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;

public interface AuthService {
    ApiResponse register(UserRegisterRequestDto requestDto);

    ApiResponse checkEmailIsExist(String email);

    ApiResponse checkPhoneNumberIsExist(String phoneNumber);

    ApiResponse checkNickNameIsExist(String nickName);
}
