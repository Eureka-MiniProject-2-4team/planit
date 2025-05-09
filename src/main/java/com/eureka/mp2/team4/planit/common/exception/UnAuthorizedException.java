package com.eureka.mp2.team4.planit.common.exception;

import static com.eureka.mp2.team4.planit.common.Message.UNAUTHORIZED;

// 로그인 하지 않은 사용자가 api 요청할 경우 => 401
public class UnAuthorizedException extends RuntimeException{
    public UnAuthorizedException() {
        super(UNAUTHORIZED);
    }
}
