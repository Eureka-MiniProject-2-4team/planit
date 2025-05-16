package com.eureka.mp2.team4.planit.auth.controller;

import com.eureka.mp2.team4.planit.auth.dto.request.*;
import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.AuthService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입, 로그인, 비밀번호 찾기 등 인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호 등으로 회원가입을 수행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterRequestDto requestDto,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());
        }
        ApiResponse response = authService.register(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "이메일 중복 확인", description = "입력한 이메일이 이미 존재하는지 확인합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam("value") String email) {
        ApiResponse response = authService.checkEmailIsExist(email);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "전화번호 중복 확인", description = "입력한 전화번호가 이미 존재하는지 확인합니다.")
    @GetMapping("/check-phoneNumber")
    public ResponseEntity<?> checkPhone(@RequestParam("value") String phoneNumber) {
        ApiResponse response = authService.checkPhoneNumberIsExist(phoneNumber);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임이 이미 존재하는지 확인합니다.")
    @GetMapping("/check-nickName")
    public ResponseEntity<?> checkNickname(@RequestParam("value") String nickName) {
        ApiResponse response = authService.checkNickNameIsExist(nickName);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 검증", description = "현재 비밀번호가 맞는지 확인합니다.")
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal PlanitUserDetails planitUserDetails,
                                            @RequestBody VerifyPasswordRequestDto requestDto) {
        ApiResponse response = authService.verifyPassword(planitUserDetails.getUsername(), requestDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "이메일 찾기", description = "사용자 정보(이름, 전화번호)로 이메일을 조회합니다.")
    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestBody FindEmailRequestDto requestDto) {
        ApiResponse response = authService.findEmail(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 찾기", description = "비밀번호 재설정을 위한 이메일을 발송합니다.")
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequestDto requestDto) {
        ApiResponse response = authService.findPassword(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "비밀번호 재설정", description = "비밀번호 재설정 링크를 통해 비밀번호를 재설정합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        ApiResponse response = authService.resetPassword(requestDto);
        return ResponseEntity.ok().body(response);
    }
}
