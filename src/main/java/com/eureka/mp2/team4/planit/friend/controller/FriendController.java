package com.eureka.mp2.team4.planit.friend.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.annotations.FriendCheck;
import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 친구 요청 보내기
    @PostMapping
    public ResponseEntity<ApiResponse> sendRequest(@AuthenticationPrincipal PlanitUserDetails userDetails, @RequestBody FriendAskDto dto) {
        return ResponseEntity.ok(friendService.sendRequest(userDetails.getUsername(),dto.getReceiverId()));
    }

    // 친구 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getMyFriends(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getUsername()));
    }

    // 받은 친구 요청 목록 (수락 대기)
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getReceivedRequests(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userDetails.getUsername()));
    }

    // 내가 보낸 친구 요청 목록
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse> getSentRequests(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getSentRequests(userDetails.getUsername()));
    }

    // 친구 요청 수락/거절
    @FriendCheck(
            paramType = FriendParamType.FRIEND_ID,
            mustBe = FriendRole.RECEIVER,
            requiredStatus = FriendStatus.PENDING
    )
    @PatchMapping("/{friendId}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable String friendId,
                                                          @RequestBody FriendUpdateStatusDto dto) {
        return ResponseEntity.ok(friendService.updateStatus(friendId, dto));
    }

    // 친구 삭제
    @FriendCheck(
            paramType = FriendParamType.FRIEND_ID,
            mustBe = FriendRole.ANY,
            requiredStatus = FriendStatus.ACCEPTED
    )
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String friendId) {
        return ResponseEntity.ok(friendService.delete(friendId));
    }

    // 친구 검색 (닉네임 또는 이메일 기반)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchFriends(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                     @RequestParam String keyword) {
        return ResponseEntity.ok(friendService.searchFriends(userDetails.getUsername(), keyword));
    }
}
