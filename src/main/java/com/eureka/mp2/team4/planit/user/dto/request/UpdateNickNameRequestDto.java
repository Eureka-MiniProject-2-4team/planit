package com.eureka.mp2.team4.planit.user.dto.request;

import com.eureka.mp2.team4.planit.auth.constants.Constraints;
import com.eureka.mp2.team4.planit.auth.constants.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateNickNameRequestDto {
    @NotBlank
    @Size(min = Constraints.NICKNAME_MIN, max = Constraints.NICKNAME_MAX)
    @Pattern(regexp = Constraints.NICKNAME_REGEX, message = Messages.INVALID_NICKNAME)
    private String newNickName;
}
