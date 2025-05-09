package com.eureka.mp2.team4.planit.common.exception;

// 데이터베이스 오류 => 500
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(message);
    }
}
