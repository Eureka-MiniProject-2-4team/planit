package com.eureka.mp2.team4.planit.user.mapper;

import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void register(UserRegisterRequestDto user);

    boolean isExistEmail(String email);

    boolean isExistPhoneNumber(String phoneNumber);

    boolean isExistNickName(String nickName);

    UserDto findByEmail(String email);

    UserDto findById(String userId);

    void updateNickName(@Param("userId") String userId, @Param("newNickName") String newNickName);

    void updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword);
}
