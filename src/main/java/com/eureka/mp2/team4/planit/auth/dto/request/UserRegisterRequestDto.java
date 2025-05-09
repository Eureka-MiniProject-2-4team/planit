package com.eureka.mp2.team4.planit.auth.dto.request;

import com.eureka.mp2.team4.planit.auth.constants.Constraints;
import com.eureka.mp2.team4.planit.auth.constants.Messages;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterRequestDto {
    private String id;

    @NotBlank
    @Pattern(regexp = Constraints.EMAIL_REGEX, message = Messages.INVALID_EMAIL)
    private String email;

    @NotBlank
    @Size(min = Constraints.USERNAME_MIN, max = Constraints.USERNAME_MAX)
    @Pattern(regexp = Constraints.USERNAME_REGEX, message = Messages.INVALID_USERNAME)
    private String username;

    @NotBlank
    @Size(min = Constraints.PASSWORD_MIN)
    @Pattern(regexp = Constraints.PASSWORD_REGEX, message = Messages.INVALID_PASSWORD)
    private String password;

    @NotBlank
    @Size(min = Constraints.NICKNAME_MIN, max = Constraints.NICKNAME_MAX)
    @Pattern(regexp = Constraints.NICKNAME_REGEX, message = Messages.INVALID_NICKNAME)
    private String nickname;

    @NotBlank
    @Pattern(regexp = Constraints.PHONE_REGEX, message = Messages.INVALID_PHONE)
    private String phoneNumber;

    private UserRole role;
}