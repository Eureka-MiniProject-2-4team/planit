package com.eureka.mp2.team4.planit.user.mapper;

import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void register(UserRegisterRequestDto user);

    boolean isExistEmail(String email);

    boolean isExistPhoneNumber(String phoneNumber);

    boolean isExistNickName(String nickName);
}
