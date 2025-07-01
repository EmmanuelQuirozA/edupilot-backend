package com.monarchsolutions.sms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Inject the secret from application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Token validity in milliseconds (e.g., 1 hour)
    private final long jwtExpirationMs = 604800000; // 7 days

    // The secret key that will be generated securely
    private SecretKey key;

    // Initialize the secure key using the Keys utility
    @PostConstruct
    public void init() {
        System.out.println("JWT Secret from properties: " + jwtSecret);
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    // Generate a token containing username and role (or other claims)
    public String generateToken(Long userId, Long schoolId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("schoolId", schoolId);
        claims.put("role", role);
        return Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key)
                    .compact();
    }

    // Extract username from token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extract custom claim (e.g., role)
    public String extractUserRole(String token) {
        return (String) getClaims(token).get("role");
    }

    public Long extractSchoolId(String token) {
        Claims claims = getClaims(token);
        return claims.get("schoolId", Long.class);
    }

    public Long extractUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Use the new parser builder API to get claims
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
