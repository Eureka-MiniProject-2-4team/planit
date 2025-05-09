package com.eureka.mp2.team4.planit.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {
    private Result result;
    private String message;
    private T data;
}
