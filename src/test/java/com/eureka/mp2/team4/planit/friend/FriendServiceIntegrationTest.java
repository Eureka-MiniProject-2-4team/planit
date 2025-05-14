package com.eureka.mp2.team4.planit.friend;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.constants.FriendMessages;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FriendServiceIntegrationTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUpUsers() {
        jdbcTemplate.update("DELETE FROM friend");
        jdbcTemplate.update("DELETE FROM users WHERE id IN (?, ?)", "user-123", "user-456");

        jdbcTemplate.update("INSERT INTO users (id, email, username, password, nickname, role, created_at, updated_at, is_active, phone_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)",
                "user-123", "testuser@example.com", "testuser", "encoded-password", "테스트유저", "USER", 1, "010-1234-5678");

        jdbcTemplate.update("INSERT INTO users (id, email, username, password, nickname, role, created_at, updated_at, is_active, phone_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?)",
                "user-456", "receiver@example.com", "receiver", "encoded-password2", "리시버유저", "USER", 1, "010-5678-1234");

        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT id FROM users");
        System.out.println("현재 users 테이블: " + users);
    }

    @Test
    @DisplayName("친구 요청부터 수락, 삭제까지 통합 흐름")
    void friendRequest_accept_delete_fullFlow() {
        String requesterId = "user-123";
        String receiverId = "user-456";

        // 1. 친구 요청
        ApiResponse sendResponse = friendService.sendRequest(requesterId, receiverId);
        assertEquals(Result.SUCCESS, sendResponse.getResult(), "친구 요청 실패: " + sendResponse.getMessage());

        // 2. 받은 친구 요청 조회
        FriendListResponseDto pending = friendService.getReceivedRequests(receiverId).getData();
        String friendId = pending.getFriends().stream()
                .filter(f -> f.getRequesterId().equals(requesterId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("요청자가 보낸 친구 요청이 없음"))
                .getId();

        // 3. 친구 요청 수락
        FriendUpdateStatusDto updateDto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.ACCEPTED)
                .build();

        ApiResponse updateResponse = friendService.updateStatus(friendId, updateDto);
        assertEquals(Result.SUCCESS, updateResponse.getResult(), "친구 수락 실패: " + updateResponse.getMessage());

        // 4. 친구 목록 조회
        FriendListResponseDto friends = friendService.getFriends(receiverId).getData();
        assertTrue(friends.getFriends().stream().anyMatch(f -> f.getId().equals(friendId)), "친구 목록에 존재하지 않음");

        // 5. 친구 삭제
        ApiResponse deleteResponse = friendService.delete(friendId);
        assertEquals(Result.SUCCESS, deleteResponse.getResult(), "친구 삭제 실패: " + deleteResponse.getMessage());
    }

    @Test
    @DisplayName("친구 요청 중복 전송 실패")
    void duplicateFriendRequest_shouldFail() {
        String requesterId = "user-123";
        String receiverId = "user-456";

        ApiResponse first = friendService.sendRequest(requesterId, receiverId);
        assertEquals(Result.SUCCESS, first.getResult(), "첫 요청 실패: " + first.getMessage());

        ApiResponse second = friendService.sendRequest(requesterId, receiverId);
        assertEquals(Result.FAIL, second.getResult(), "중복 요청이 실패하지 않음");

        // 허용 가능한 중복 실패 메시지 목록
        List<String> validMessages = List.of(
                FriendMessages.ALREADY_SENT_REQUEST,
                FriendMessages.REQUEST_EXISTS_FROM_RECEIVER,
                FriendMessages.ALREADY_FRIEND
        );
        assertTrue(validMessages.contains(second.getMessage()), "예상하지 못한 실패 메시지: " + second.getMessage());
    }
}
