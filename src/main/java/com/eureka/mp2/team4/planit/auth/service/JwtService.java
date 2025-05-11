package com.eureka.mp2.team4.planit.auth.service;

import com.eureka.mp2.team4.planit.user.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.eureka.mp2.team4.planit.auth.constants.Constraints.*;

@Component
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    private long expiration = EXPIRATION;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String id, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim(JWT_ID_FIELD, id)
                .claim(JWT_ROLE_FILED, role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get(JWT_ID_FIELD, String.class);
    }

    public String getUserRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get(JWT_ROLE_FILED, String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("토큰이 만료되었습니다: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("JWT 예외 발생: {}", e.getMessage());
            return false;
        }
    }
}
