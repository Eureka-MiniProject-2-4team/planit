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
        FriendDto friendDto = null;
        // todo 유민님이 구현한 두명아이디로 조회 매퍼 사용
        if (friendDto == null)
            return null;

        return friendDto.getStatus();
    }
}
