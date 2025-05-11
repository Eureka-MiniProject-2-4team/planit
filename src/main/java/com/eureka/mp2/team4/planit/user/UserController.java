package com.eureka.mp2.team4.planit.user;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/me")
    public UserDto getUserInfo(@AuthenticationPrincipal PlanitUserDetails userDetails) {
        return userDetails.getUserDto();
    }
}
