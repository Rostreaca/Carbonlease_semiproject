package com.kh.token.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] arr = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(arr);
    }

    // Access Token 생성
    public String getAccessToken(String memberNo) {
        return Jwts.builder()
                .subject(String.valueOf(memberNo))
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(Duration.ofDays(1))))
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String getRefreshToken(String memberNo) {
        return Jwts.builder()
                .subject(String.valueOf(memberNo))
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(Duration.ofDays(3))))
                .signWith(key)
                .compact();
    }

    public Claims paresJwt(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

