package com.eureka.mp2.team4.planit.todo.personal.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.annotations.FriendCheck;
import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.service.PersonalTodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class PersonalTodoController {

    private final PersonalTodoService personalTodoService;

    // 자신 todo 생성
    @PostMapping("/me")
    public ResponseEntity<ApiResponse> create(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.create(userDetails.getUsername(), request));
    }

    // 자신 todo 수정
    @PatchMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> update(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @PathVariable String todoId,
                                              @RequestBody PersonalTodoRequestDto request) {
        return ResponseEntity.ok(personalTodoService.update(userDetails.getUsername(), todoId, request));
    }

    // 자신 todo 삭제
    @DeleteMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> delete(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                              @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.delete(userDetails.getUsername(), todoId));
    }

    // 자신 todo 개별 조회
    @GetMapping("/me/{todoId}")
    public ResponseEntity<ApiResponse> getById(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                               @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.getById(userDetails.getUsername(), todoId));
    }

    // 자신 todo 전체 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getAllByMe(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return ResponseEntity.ok(personalTodoService.getAllByUser(userDetails.getUsername()));
    }

    // 친구 todo 전체 조회
    @GetMapping("/{userId}")
    @FriendCheck(paramType = FriendParamType.USER_ID, mustBe = FriendRole.ANY, requiredStatus = FriendStatus.ACCEPTED)
    public ResponseEntity<ApiResponse> getAllByFriend(@PathVariable String userId) {
        return ResponseEntity.ok(personalTodoService.getAllByFriend(userId));
    }

    // 친구 todo 개별 조회
    @GetMapping("/{userId}/{todoId}")
    @FriendCheck(paramType = FriendParamType.USER_ID, mustBe = FriendRole.ANY, requiredStatus = FriendStatus.ACCEPTED)
    public ResponseEntity<ApiResponse> getFriendTodoById(@PathVariable String userId,
                                                         @PathVariable String todoId) {
        return ResponseEntity.ok(personalTodoService.getFriendTodoById(userId, todoId));
    }
}
