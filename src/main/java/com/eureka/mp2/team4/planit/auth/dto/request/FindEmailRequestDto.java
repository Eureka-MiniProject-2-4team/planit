package com.eureka.mp2.team4.planit.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindEmailRequestDto {
    private String name;
    private String phoneNumber;
}
