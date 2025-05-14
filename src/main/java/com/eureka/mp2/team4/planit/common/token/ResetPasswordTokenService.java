package com.eureka.mp2.team4.planit.common.token;

public interface ResetPasswordTokenService {
    void saveToken(String token, String email);

    String getUserIdByToken(String token);

    void removeToken(String token);
}