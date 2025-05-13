package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendQueryServiceImpl implements FriendQueryService {
    private final FriendMapper friendMapper;


    @Override
    public FriendStatus areFriends(String userId, String targetUserId) {
        FriendDto friendDto = friendMapper.findByBothUserId(userId, targetUserId);
        if (friendDto == null)
            return null;

        return friendDto.getStatus();
    }
}
