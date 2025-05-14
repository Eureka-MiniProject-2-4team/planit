package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendQueryServiceTest {

    @InjectMocks
    private FriendQueryServiceImpl friendQueryService;

    @Mock
    private FriendMapper friendMapper;

    @Test
    @DisplayName("areFriends - 친구 상태가 존재할 경우 해당 상태 반환")
    void areFriends_exist_returnsStatus() {
        // given
        String userId = "user-1";
        String targetUserId = "user-2";
        FriendDto friendDto = new FriendDto();
        friendDto.setStatus(FriendStatus.ACCEPTED);

        when(friendMapper.findByBothUserId(userId, targetUserId)).thenReturn(friendDto);

        // when
        FriendStatus result = friendQueryService.areFriends(userId, targetUserId);

        // then
        assertEquals(FriendStatus.ACCEPTED, result);
        verify(friendMapper).findByBothUserId(userId, targetUserId);
    }

    @Test
    @DisplayName("areFriends - 친구 상태가 없을 경우 null 반환")
    void areFriends_notExist_returnsNull() {
        // given
        String userId = "user-1";
        String targetUserId = "user-3";

        when(friendMapper.findByBothUserId(userId, targetUserId)).thenReturn(null);

        // when
        FriendStatus result = friendQueryService.areFriends(userId, targetUserId);

        // then
        assertNull(result);
        verify(friendMapper).findByBothUserId(userId, targetUserId);
    }

}
