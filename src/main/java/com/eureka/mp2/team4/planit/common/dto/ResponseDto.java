package com.eureka.mp2.team4.planit.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseDto<T extends BaseDto> {
    T data;
}
