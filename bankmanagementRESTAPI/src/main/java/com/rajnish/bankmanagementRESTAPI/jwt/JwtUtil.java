package com.rajnish.bankmanagementRESTAPI.jwt;

import com.rajnish.bankmanagementRESTAPI.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.SECRET}")
    private String SECRET;

    private SecretKey getSecretKey(String secretKey){
//        System.out.println("secret: "+SECRET);
        //It creates a cryptographic secret key from your plain string
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
//        return Jwts.SIG.HS256.key().build();
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
        return username.equals(extractUsername(token)) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey(SECRET)).build().parseSignedClaims(token).getPayload();
    }
}
