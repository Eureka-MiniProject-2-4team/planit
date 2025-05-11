package com.eureka.mp2.team4.planit.friend;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.constants.FriendMessages;
import com.eureka.mp2.team4.planit.friend.dto.FriendsDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FriendServiceIntegrationTest {

    @Autowired
    private FriendService friendService;

    @DisplayName("친구 요청부터 수락, 삭제까지 통합 흐름")
    @Test
    void friendRequest_accept_delete_fullFlow() {
        String requesterId = "user-123";
        String receiverId = "user-456";

        // 친구 요청
        FriendAskDto askDto = FriendAskDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();

        ApiResponse sendResponse = friendService.sendRequest(askDto);
        assertEquals(Result.SUCCESS, sendResponse.getResult());

        // 수락 대기 목록에서 ID 가져오기
        FriendsDto pending = friendService.getReceivedRequests(receiverId).getData();
        String friendId = pending.getFriends().stream()
                .filter(f -> f.getRequesterId().equals(requesterId))
                .findFirst()
                .orElseThrow()
                .getId();

        // 수락 처리
        FriendUpdateStatusDto updateDto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.ACCEPTED)
                .build();

        ApiResponse updateResponse = friendService.updateStatus(friendId, updateDto);
        assertEquals(Result.SUCCESS, updateResponse.getResult());

        // 친구 목록 조회
        FriendsDto friends = friendService.getFriends(receiverId).getData();
        assertTrue(friends.getFriends().stream().anyMatch(f -> f.getId().equals(friendId)));

        // 삭제
        ApiResponse deleteResponse = friendService.delete(friendId);
        assertEquals(Result.SUCCESS, deleteResponse.getResult());
    }

    @DisplayName("친구 요청 중복 전송 실패")
    @Test
    void duplicateFriendRequest_shouldFail() {
        String requesterId = "user-123";
        String receiverId = "user-456";

        FriendAskDto dto = FriendAskDto.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();

        // 첫 요청 성공
        ApiResponse first = friendService.sendRequest(dto);
        assertEquals(Result.SUCCESS, first.getResult());

        // 중복 요청 실패
        ApiResponse second = friendService.sendRequest(dto);
        assertEquals(Result.FAIL, second.getResult());
        assertEquals(FriendMessages.REQUEST_ALREADY_EXISTS, second.getMessage());
    }
}
