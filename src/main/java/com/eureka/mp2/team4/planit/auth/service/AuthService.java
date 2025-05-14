package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.auth.dto.request.*;
import com.eureka.mp2.team4.planit.common.ApiResponse;

public interface AuthService {
    ApiResponse register(UserRegisterRequestDto requestDto);

    ApiResponse checkEmailIsExist(String email);

    ApiResponse checkPhoneNumberIsExist(String phoneNumber);

    ApiResponse checkNickNameIsExist(String nickName);

    ApiResponse verifyPassword(String userId, VerifyPasswordRequestDto requestDto);

    ApiResponse findEmail(FindEmailRequestDto requestDto);

    ApiResponse findPassword(FindPasswordRequestDto requestDto);

    ApiResponse resetPassword(ResetPasswordRequestDto requestDto);

}
