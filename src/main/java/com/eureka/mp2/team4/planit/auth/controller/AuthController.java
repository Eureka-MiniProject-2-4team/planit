package com.eureka.mp2.team4.planit.auth.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.auth.dto.request.UserRegisterRequestDto;
import com.eureka.mp2.team4.planit.auth.service.AuthService;

import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("value") String nickName) {
        ApiResponse response = authService.checkNickNameIsExist(nickName);
        return ResponseEntity.ok().body(response);
    }
}
