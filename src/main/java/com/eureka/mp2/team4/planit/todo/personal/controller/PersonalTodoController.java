package com.eureka.mp2.team4.planit.todo.personal.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.annotations.FriendCheck;
import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.service.PersonalTodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Personal Todo", description = "개인 투두 기능 API (생성, 수정, 삭제, 조회 등)")
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class PersonalTodoController {

    private final PersonalTodoService personalTodoService;

    @Operation(summary = "자신의 투두 생성", description = "사용자가 자신의 투두를 생성합니다.")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse> create(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.create(userDetails.getUsername(), request));
    }

    @Operation(summary = "자신의 투두 수정", description = "사용자가 자신의 투두를 수정합니다.")
    @PatchMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> update(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @PathVariable String todoId,
                                              @RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.update(userDetails.getUsername(), todoId, request));
    }

    @Operation(summary = "자신의 투두 삭제", description = "사용자가 자신의 투두를 삭제합니다.")
    @DeleteMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.delete(userDetails.getUsername(), todoId));
    }

    @Operation(summary = "자신의 투두 개별 조회", description = "사용자가 자신의 투두를 개별 조회합니다.")
    @GetMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> getById(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                               @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.getById(userDetails.getUsername(), todoId));
    }

    @Operation(summary = "자신의 투두 전체 조회", description = "사용자가 자신의 모든 투두를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getAllByMe(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(personalTodoService.getAllByUser(userDetails.getUsername()));
    }

    @Operation(summary = "친구의 투두 전체 조회", description = "친구의 모든 투두를 조회합니다.")
    @GetMapping("/{userId}")
    @FriendCheck(paramType = FriendParamType.USER_ID, mustBe = FriendRole.ANY, requiredStatus = FriendStatus.ACCEPTED)
    public ResponseEntity<ApiResponse> getAllByFriend(@PathVariable String userId) {
        return ResponseEntity.ok(personalTodoService.getAllByFriend(userId));
    }

    @Operation(summary = "친구의 투두 개별 조회", description = "친구의 특정 투두를 조회합니다.")
    @GetMapping("/{userId}/{todoId}")
    @FriendCheck(paramType = FriendParamType.USER_ID, mustBe = FriendRole.ANY, requiredStatus = FriendStatus.ACCEPTED)
    public ResponseEntity<ApiResponse> getFriendTodoById(@PathVariable String userId,
                                                         @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.getFriendTodoById(userId, todoId));
    }
}
