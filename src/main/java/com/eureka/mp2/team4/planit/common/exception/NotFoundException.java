package com.eureka.mp2.team4.planit.common.exception;

// 데이터 존재 x (ex : 해당 아이디의 유저가 없을 때) => 404
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
