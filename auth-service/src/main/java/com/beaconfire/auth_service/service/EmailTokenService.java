package com.beaconfire.auth_service.service;

import com.beaconfire.auth_service.entity.Token;
import com.beaconfire.auth_service.exception.InvalidTokenException;
import com.beaconfire.auth_service.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailTokenService {

    private final TokenRepository tokenRepository;

    @Value("${token.expiration.hours}")
    private int expirationHours;

    @Autowired
    public EmailTokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateAndSaveToken(String email) {
        return tokenRepository.findByEmail(email)
                .map(this::renewTokenIfExpired)
                .orElseGet(() -> createAndSaveNewToken(email));
    }

    private String renewTokenIfExpired(Token existingToken) {
        if (isTokenExpired(existingToken)) {
            String newToken = generateToken();
            updateToken(existingToken, newToken);
            return newToken;
        }
        return existingToken.getToken();
    }

    private boolean isTokenExpired(Token token) {
        return token.getExpiresAt().isBefore(LocalDateTime.now());
    }

    private void updateToken(Token token, String newToken) {
        token.setToken(newToken);
        token.setExpiresAt(LocalDateTime.now().plusHours(expirationHours));
        tokenRepository.save(token);
    }

    private String createAndSaveNewToken(String email) {
        String newToken = generateToken();
        Token tokenEntity = new Token(email, newToken, expirationHours);
        tokenRepository.save(tokenEntity);
        return newToken;
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public Token validAndGetTokenEntity(String token) {
        return tokenRepository.findByToken(token)
                .filter(this::isTokenValid)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token."));
    }

    private boolean isTokenValid(Token token) {
        if (isTokenExpired(token)) {
            throw new InvalidTokenException("Token has expired.");
        }
        return true;
    }
}