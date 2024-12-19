package com.beaconfire.auth_service.controller;

import com.beaconfire.auth_service.dto.AuthRequest;
import com.beaconfire.auth_service.dto.RegisterRequest;
import com.beaconfire.auth_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.addNewUser(registerRequest);
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