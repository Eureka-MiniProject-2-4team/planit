package com.eureka.mp2.team4.planit.common.exception;

import static com.eureka.mp2.team4.planit.common.Message.TOKEN_EXPIRED;

// jwt 토큰이 만료되었을 경우 = 401
public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException() {
        super(TOKEN_EXPIRED);
    }
}
