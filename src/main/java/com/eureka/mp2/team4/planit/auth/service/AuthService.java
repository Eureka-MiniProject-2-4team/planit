package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.auth.dto.request.FindEmailRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.auth.dto.request.VerifyPasswordRequestDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;

public interface AuthService {
    ApiResponse register(UserRegisterRequestDto requestDto);

    ApiResponse checkEmailIsExist(String email);

    ApiResponse checkPhoneNumberIsExist(String phoneNumber);

    ApiResponse checkNickNameIsExist(String nickName);

    ApiResponse verifyPassword(String userId, VerifyPasswordRequestDto requestDto);

    ApiResponse findEmail(FindEmailRequestDto requestDto);
}
