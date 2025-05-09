package com.eureka.mp2.team4.planit.common.exception;

import lombok.Getter;

@Getter
public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String message) {
        super(message);
    }

}
