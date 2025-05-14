package com.eureka.mp2.team4.planit.common.token;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryResetPasswordTokenService implements ResetPasswordTokenService {
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public void saveToken(String token, String id) {
        tokenStore.put(token, id);
    }

    public String getUserIdByToken(String token) {
        return tokenStore.get(token);
    }

    public void removeToken(String token) {
        tokenStore.remove(token);
    }
}
