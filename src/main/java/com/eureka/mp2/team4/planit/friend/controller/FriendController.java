package com.eureka.mp2.team4.planit.friend.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @PostMapping
    public ResponseEntity<ApiResponse> sendRequest(@RequestBody FriendAskDto dto) {
        return ResponseEntity.ok(friendService.sendRequest(dto));
    }

    // 친구 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getMyFriends(String userId) {
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    // 받은 친구 요청 목록 (수락 대기)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getReceivedRequests(String userId) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userId));
    }

    // 내가 보낸 친구 요청 목록
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse> getSentRequests(String userId) {
        return ResponseEntity.ok(friendService.getSentRequests(userId));
    }

    // 친구 요청 수락/거절
    @PatchMapping("/{friendId}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable String friendId,
                                                          @RequestBody FriendUpdateStatusDto dto) {
        return ResponseEntity.ok(friendService.updateStatus(friendId, dto));
    }

    // 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String friendId) {
        return ResponseEntity.ok(friendService.delete(friendId));
    }
}
