package com.eureka.mp2.team4.planit.auth.controller;

import com.eureka.mp2.team4.planit.auth.dto.request.*;
import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.auth.service.AuthService;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterRequestDto requestDto,
                                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());
        }
        ApiResponse response = authService.register(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam("value") String email) {
        ApiResponse response = authService.checkEmailIsExist(email);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/check-phoneNumber")
    public ResponseEntity<?> checkPhone(@RequestParam("value") String phoneNumber) {
        ApiResponse response = authService.checkPhoneNumberIsExist(phoneNumber);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/check-nickName")
    public ResponseEntity<?> checkNickname(@RequestParam("value") String nickName) {
        ApiResponse response = authService.checkNickNameIsExist(nickName);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@AuthenticationPrincipal PlanitUserDetails planitUserDetails,
                                            @RequestBody VerifyPasswordRequestDto requestDto) {
        ApiResponse response = authService.verifyPassword(planitUserDetails.getUsername(), requestDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/find-email")
    public ResponseEntity<?> findEmail(@RequestBody FindEmailRequestDto requestDto) {
        ApiResponse response = authService.findEmail(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequestDto requestDto) {
        ApiResponse response = authService.findPassword(requestDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto requestDto) {
        ApiResponse response = authService.resetPassword(requestDto);
        return ResponseEntity.ok().body(response);
    }

}
