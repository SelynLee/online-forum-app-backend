package com.beaconfire.auth_service.controller;

import com.beaconfire.auth_service.dto.AuthRequest;
import com.beaconfire.auth_service.dto.RegisterRequest;
import com.beaconfire.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        ResponseEntity<String> result = authService.addNewUser(registerRequest);
        Map<String, String> response = Collections.singletonMap("message", result.getBody());
        return ResponseEntity.status(result.getStatusCode()).body(response);
    }

    @PatchMapping("/validate")
    public ResponseEntity<String> activateUserByToken(@RequestParam("token") String token) {
        return authService.activateUserByToken(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid AuthRequest authRequest) {
        return authService.authenticateUserAndGenerateJwt(authRequest);
    }
}