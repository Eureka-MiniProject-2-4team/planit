package com.eureka.mp2.team4.planit.common.exception;

// 잘못된 입력값 => 400
public class InvalidInputException extends RuntimeException{
    public InvalidInputException(String message) {
        super(message);
    }
}
