package com.beaconfire.auth_service.service;

import com.beaconfire.auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.ttl.millis}")
    private long ttlMillis;

    @Value("${jwt.secret}")
    private String secret;

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    public String createJwt(User user) {
        String id = generateJwtId();
        Date now = getCurrentDate();
        String email = user.getEmail();
        String userType = user.getUserType().toString();
        Key signingKey = getSigningKey();

        return buildJwt(id, now, email, userType, signingKey);
    }

    private String generateJwtId() {
        return UUID.randomUUID().toString();
    }

    private Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Key getSigningKey() {
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(apiKeySecretBytes, SIGNATURE_ALGORITHM.getJcaName());
    }

    private String buildJwt(String id, Date now, String email, String userType, Key signingKey) {
        Date expirationDate = getExpirationDate();

        return Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(email)
                .claim("userType", userType)
                .setIssuer(jwtIssuer)
                .setExpiration(expirationDate)
                .signWith(SIGNATURE_ALGORITHM, signingKey)
                .compact();
    }

    private Date getExpirationDate() {
        long expirationMillis = System.currentTimeMillis() + ttlMillis;
        return new Date(expirationMillis);
    }
}