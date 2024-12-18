package com.beaconfire.auth_service.repository;

import com.beaconfire.auth_service.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    Optional<Token> findByEmail(String email);
}