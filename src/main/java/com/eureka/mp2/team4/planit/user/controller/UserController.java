package com.eureka.mp2.team4.planit.user.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 관련 API (내 정보 조회/수정/탈퇴 등)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyPage(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        UserResponseDto userResponseDto = userService.getMyPageData(userDetails.getUsername());
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .data(userResponseDto)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "내 정보 수정", description = "이름, 닉네임 등의 내 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<?>> updateUser(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                     @Valid @RequestBody UpdateUserRequestDto requestDto,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());

        ApiResponse apiResponse = userService.updateUser(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<?>> deleteUser(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        ApiResponse apiResponse = userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호와 새 비밀번호를 입력하여 비밀번호를 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                         @Valid @RequestBody UpdatePasswordRequestDto requestDto,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());

        ApiResponse apiResponse = userService.updatePassword(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "유저 정보 조회", description = "특정 유저의 정보를 닉네임 또는 이메일로 조회합니다. 팀 ID를 파라미터로 넘길 경우 팀 소속 여부도 판단합니다.")
    @GetMapping("/{value}")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                      @PathVariable("value") String value,
                                                      @RequestParam(required = false) String teamId) {

        ApiResponse apiResponse = userService.getUserInfo(userDetails.getUsername(), value, teamId);
        return ResponseEntity.ok(apiResponse);
    }
}
