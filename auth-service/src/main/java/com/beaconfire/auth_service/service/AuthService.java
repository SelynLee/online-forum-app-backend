package com.beaconfire.auth_service.service;

import com.beaconfire.auth_service.dto.AuthRequest;
import com.beaconfire.auth_service.dto.EmailRequest;
import com.beaconfire.auth_service.dto.RegisterRequest;
import com.beaconfire.auth_service.entity.Token;
import com.beaconfire.auth_service.entity.User;
import com.beaconfire.auth_service.entity.UserType;
import com.beaconfire.auth_service.exception.InvalidAccessException;
import com.beaconfire.auth_service.exception.UserAlreadyExistsException;
import com.beaconfire.auth_service.exception.InvalidTokenException;
import com.beaconfire.auth_service.exception.UserIsNotActive;
import com.beaconfire.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final PasswordEncoder passwordEncoder;
    private final EmailTokenService emailTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${verification.email.url}")
    private String verificationUrl;

    @Autowired
    public AuthService(UserRepository userRepository, RabbitMQProducer rabbitMQProducer, PasswordEncoder passwordEncoder,
                       EmailTokenService emailTokenService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.rabbitMQProducer = rabbitMQProducer;
        this.passwordEncoder = passwordEncoder;
        this.emailTokenService = emailTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public ResponseEntity<String> addNewUser(RegisterRequest registerRequest) {
        validateUserExistence(registerRequest.getEmail());

        String token = emailTokenService.generateAndSaveToken(registerRequest.getEmail());
        sendVerificationEmail(registerRequest, token);

        User newUser = buildNewUser(registerRequest);
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully.");
    }

    private void validateUserExistence(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isActive()) {
                throw new UserAlreadyExistsException("User with this email already exists.");
            }
        });
    }

    private void sendVerificationEmail(RegisterRequest registerRequest, String token) {
        EmailRequest emailRequest = new EmailRequest(
                registerRequest.getEmail(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                generateVerificationUrl(token)
        );
        rabbitMQProducer.sendMessage(emailRequest);
    }

    private String generateVerificationUrl(String token) {
        return verificationUrl + token;
    }

    private User buildNewUser(RegisterRequest registerRequest) {
        Optional<User> existingUser = userRepository.findByEmail(registerRequest.getEmail());
        User newUser = convertToEntity(registerRequest);

        if (existingUser.isPresent()) {
            newUser.setUserId(existingUser.get().getUserId());
        }
        newUser.setDateJoined(LocalDateTime.now());

        return newUser;
    }

    private User convertToEntity(RegisterRequest registerRequest) {
        return new User(
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                UserType.VISITOR,
                false
        );
    }

    public ResponseEntity<String> activateUserByToken(String token) {
        Token tokenEntity = emailTokenService.validAndGetTokenEntity(token);
        User user = findUserByEmail(tokenEntity.getEmail());

        if (user.isActive()) {
            return ResponseEntity.status(HttpStatus.OK).body("User is already activated.");
        }

        user.setActive(true);
        user.setUserType(UserType.NORMAL);
        user.setDateJoined(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body("User activated successfully.");
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("User associated with this token does not exist."));
    }

    public ResponseEntity<String> authenticateUserAndGenerateJwt(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new InvalidAccessException("Invalid username or password.");
        }

        User user = findUserByEmail(authRequest.getEmail());
        if (!user.isActive()) {
            throw new UserIsNotActive("User account is not activated.");
        }

        String jwt = jwtService.createJwt(user);
        return ResponseEntity.status(HttpStatus.OK).body(jwt);
    }
}