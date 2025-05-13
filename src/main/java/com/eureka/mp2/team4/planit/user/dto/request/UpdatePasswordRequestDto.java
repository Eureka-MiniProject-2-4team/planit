package com.eureka.mp2.team4.planit.user.dto.request;

import com.eureka.mp2.team4.planit.auth.constants.Constraints;
import com.eureka.mp2.team4.planit.auth.constants.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.eureka.mp2.team4.planit.auth.constants.Constraints.PASSWORD_MIN;
import static com.eureka.mp2.team4.planit.user.constants.Messages.INPUT_NEW_PASSWORD;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = INPUT_NEW_PASSWORD)
    @Pattern(regexp = Constraints.PASSWORD_REGEX, message = Messages.INVALID_PASSWORD)
    @Size(min = PASSWORD_MIN)
    private String newPassword;
}
