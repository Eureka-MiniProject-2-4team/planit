package com.eureka.mp2.team4.planit.common.exception;

// 중복된 값 => 409
public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String message) {
        super(message);
    }

}
