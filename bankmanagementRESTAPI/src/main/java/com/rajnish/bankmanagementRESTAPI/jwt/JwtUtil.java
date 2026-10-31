package com.rajnish.bankmanagementRESTAPI.jwt;

import com.rajnish.bankmanagementRESTAPI.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET;

    @PostConstruct
    void validateSecret() {
        if (SECRET == null || SECRET.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes.");
        }
    }

    private SecretKey getSecretKey(String secretKey){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId",user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSecretKey(SECRET))
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = getClaims(token);
        Date expiration = claims.getExpiration();
        return username.equals(claims.getSubject())
                && expiration != null
                && expiration.after(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey(SECRET)).build().parseSignedClaims(token).getPayload();
    }
}
