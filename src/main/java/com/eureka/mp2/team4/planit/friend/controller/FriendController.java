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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Friend", description = "친구 기능 API (요청, 목록 조회, 수락/거절, 삭제 등)")
@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 요청 보내기", description = "다른 사용자에게 친구 요청을 보냅니다.")
    @PostMapping
    public ResponseEntity<ApiResponse> sendRequest(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                   @RequestBody FriendAskDto dto) {
        return ResponseEntity.ok(friendService.sendRequest(userDetails.getUsername(), dto.getReceiverId()));
    }

    @Operation(summary = "친구 목록 조회", description = "수락된 친구 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse> getMyFriends(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getUsername()));
    }

    @Operation(summary = "받은 친구 요청 조회", description = "아직 수락하지 않은 받은 친구 요청 목록을 조회합니다.")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse> getReceivedRequests(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getReceivedRequests(userDetails.getUsername()));
    }

    @Operation(summary = "보낸 친구 요청 조회", description = "내가 보낸 친구 요청 목록을 조회합니다.")
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse> getSentRequests(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getSentRequests(userDetails.getUsername()));
    }

    @Operation(summary = "친구 요청 수락/거절", description = "받은 친구 요청을 수락하거나 거절합니다.")
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

    @Operation(summary = "친구 삭제", description = "이미 수락된 친구 관계를 삭제합니다.")
    @FriendCheck(
            paramType = FriendParamType.FRIEND_ID,
            mustBe = FriendRole.ANY,
            requiredStatus = FriendStatus.ACCEPTED
    )
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String friendId) {
        return ResponseEntity.ok(friendService.delete(friendId));
    }
}
