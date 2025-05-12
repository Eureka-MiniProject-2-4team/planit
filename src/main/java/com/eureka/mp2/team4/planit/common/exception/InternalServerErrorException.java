package com.eureka.mp2.team4.planit.common.exception;

import static com.eureka.mp2.team4.planit.common.Message.SERVER_ERROR;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException() {
        super(SERVER_ERROR);
    }
}
