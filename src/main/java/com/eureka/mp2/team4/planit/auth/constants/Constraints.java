package com.eureka.mp2.team4.planit.auth.constants;


import jakarta.annotation.Generated;

@Generated("Excluded from coverage")
public class Constraints {

    // Validation 정규식 규약
    public static final String EMAIL_REGEX = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$";
    public static final String USERNAME_REGEX = "^[a-z가-힣]{2,20}$";
    public static final String PASSWORD_REGEX = ".*[\\W_].*";
    public static final String NICKNAME_REGEX = "^[a-z0-9가-힣]{2,12}$";
    public static final String PHONE_REGEX = "^010[0-9]{7,8}$";

    // Validation 길이 규약
    public static final int USERNAME_MIN = 2;
    public static final int USERNAME_MAX = 20;
    public static final int PASSWORD_MIN = 8;
    public static final int NICKNAME_MIN = 2;
    public static final int NICKNAME_MAX = 12;

    //jwt
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long EXPIRATION = 3600000L;
    public static final String JWT_ID_FIELD = "id";
    public static final String JWT_ROLE_FILED = "role";
} 