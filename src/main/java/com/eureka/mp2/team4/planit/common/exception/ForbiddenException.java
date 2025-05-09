package com.eureka.mp2.team4.planit.common.exception;

import static com.eureka.mp2.team4.planit.common.Message.FORBIDDEN;

// 권한이 없는 사용자 접근 => 403
public class ForbiddenException extends RuntimeException{
    public ForbiddenException() {
        super(FORBIDDEN);
    }
}
