package com.backend_spring.spring_back_test.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

public class JwtService {
    private static final String SECRET_KEY = "DAEFECEA14D4A8584E4BA902D21A065994B31861834DFECD45B9C9E85237143B";

    private static final long EXPIRATION_MS = 3600 * 1000; // 1 hora
    private static final long EXPIRATION_REMEMBER_ME_MS = 7 * 24 * 3600 * 1000; // 7 días

    public static String createToken(String email, String roles, Boolean rememberMe) {
        long now = System.currentTimeMillis();
        long exp = now + (rememberMe ? EXPIRATION_REMEMBER_ME_MS : EXPIRATION_MS);

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(now))
                .expiration(new Date(now + exp))
                .signWith(generateKey())
                .compact();
    }

    private static SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(decodedKey);

    }

    public static String getSubject(String token) {
        try {
            return getClaims(token)
                    .getSubject();
        } catch (Exception e) {
            return null; // Token inválido o expirado
        }

    }

    public static Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null; // Token inválido o expirado
        }
    }

    public static boolean isTokenValid(String token) {
        try {
            return getClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false; // Token inválido o expirado
        }
    }
}
