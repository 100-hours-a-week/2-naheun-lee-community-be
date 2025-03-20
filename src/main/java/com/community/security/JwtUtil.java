package com.community.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // ✅ 1시간 유지

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .claim("userId", userId) 
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey).build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired"); 
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token"); 
        }
    }

    public Long getUserIdFromToken(String token) { // 토큰으로 userId 가져오기
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey).build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userId", Long.class); 
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}

