package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.eureka.mp2.team4.planit.user.constants.Messages.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

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
        }catch (NotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }

    }
}
