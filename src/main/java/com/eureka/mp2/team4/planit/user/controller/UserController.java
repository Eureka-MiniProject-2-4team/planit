package com.eureka.mp2.team4.planit.user.controller;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.InvalidInputException;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserResponseDto;
import com.eureka.mp2.team4.planit.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyPage(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        UserResponseDto userResponseDto = userService.getMyPageData(userDetails.getUsername());
        ApiResponse apiResponse = ApiResponse.builder()
                .result(Result.SUCCESS)
                .data(userResponseDto)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<?>> updateUser(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                     @Valid @RequestBody UpdateUserRequestDto requestDto,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());

        ApiResponse apiResponse = userService.updateUser(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<?>> deleteUser(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        ApiResponse apiResponse = userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                         @Valid @RequestBody UpdatePasswordRequestDto requestDto,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new InvalidInputException(bindingResult.getFieldError().getDefaultMessage());

        ApiResponse apiResponse = userService.updatePassword(userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{value}")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@AuthenticationPrincipal PlanitUserDetails userDetails,
                                                      @PathVariable("value") String value,
                                                      @RequestParam(required = false) String teamId) {

        ApiResponse apiResponse = userService.getUserInfo(userDetails.getUsername(), value, teamId);
        return ResponseEntity.ok(apiResponse);
    }
}
