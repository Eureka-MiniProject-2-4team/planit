package com.eureka.mp2.team4.planit.common.email;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);
}
