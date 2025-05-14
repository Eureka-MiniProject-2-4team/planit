package com.eureka.mp2.team4.planit.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FindPasswordRequestDto {
    private String userName;
    private String email;
}
