package com.beaconfire.auth_service.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final String secret = "<BFSONLINEFORUMNPASSWORD> backward is <DROSWAPNMRUOFENILNOSFB>"; // Replace with a secure secret key
    private final long expirationMs = 10800000; 

    // Generate JWT Token
    public String generateToken(String email, String type) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", type) // Include roles in the payload
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs)) // Expiry date
                .signWith(SignatureAlgorithm.HS256, secret.getBytes()) // Signing algorithm
                .compact();
    }

    // Validate JWT Token 
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract Username from Token
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract Roles from Token
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class);
    }
}
