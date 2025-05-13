package com.eureka.mp2.team4.planit.user.dto.request;

import com.eureka.mp2.team4.planit.auth.constants.Constraints;
import com.eureka.mp2.team4.planit.auth.constants.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.eureka.mp2.team4.planit.auth.constants.Constraints.PASSWORD_MIN;
import static com.eureka.mp2.team4.planit.user.constants.Messages.INPUT_CURRENT_PASSWORD;
import static com.eureka.mp2.team4.planit.user.constants.Messages.INPUT_NEW_PASSWORD;

@Getter
@NoArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = INPUT_CURRENT_PASSWORD)
    @Pattern(regexp = Constraints.PASSWORD_REGEX, message = Messages.INVALID_PASSWORD)
    @Size(min = PASSWORD_MIN)
    private String currentPassword;

    @NotBlank(message = INPUT_NEW_PASSWORD)
    @Pattern(regexp = Constraints.PASSWORD_REGEX, message = Messages.INVALID_PASSWORD)
    @Size(min = PASSWORD_MIN)
    private String newPassword;

    public UpdatePasswordRequestDto(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
