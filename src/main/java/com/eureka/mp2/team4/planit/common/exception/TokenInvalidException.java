package com.eureka.mp2.team4.planit.common.exception;

import static com.eureka.mp2.team4.planit.common.Message.TOKEN_INVALID;

// jwt 유효하지 않을 경우 => 401
public class TokenInvalidException extends RuntimeException{
    public TokenInvalidException() {
        super(TOKEN_INVALID);
    }
}
