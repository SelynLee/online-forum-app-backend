package com.beaconfire.users_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.dto.DataResponse;
import com.beaconfire.users_service.dto.UserDTO;
//import com.beaconfire.users_service.security.JwtProvider;
import com.beaconfire.users_service.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;
//    private final JwtProvider jwtProvider;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
//        this.jwtProvider = jwtProvider;
    }

    // Endpoint for user registration (signup)
    @PostMapping("/signup")
    public ResponseEntity<DataResponse> registerUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    DataResponse.builder()
                            .success(false)
                            .message("Validation failed")
                            .data(result.getAllErrors())
                            .build()
            );
        }

        try {
            // Register the user and return UserDTO
            UserDTO registeredUserDTO = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    DataResponse.builder()
                            .success(true)
                            .message("User registered successfully")
                            .data(registeredUserDTO) // Include the registered user's DTO in the response
                            .build()
            );
        } catch (IllegalArgumentException e) {
            // Handle duplicate email exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DataResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }
 // Endpoint to fetch user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<DataResponse> getUserById(@PathVariable("id") Integer userId) {
        try {
            // Fetch the user by ID
            UserDTO userDTO = userService.findUserById(userId);
            return ResponseEntity.ok(
                    DataResponse.builder()
                            .success(true)
                            .message("User fetched successfully")
                            .data(userDTO)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            // Handle user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DataResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred.")
                            .build()
            );
        }
    }

    // Endpoint for user login
//    @PostMapping("/login")
//    public ResponseEntity<DataResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
//        try {
//            // Authenticate the user and generate JWT token
//            UserDTO userDTO = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
//            String token = jwtProvider.generateToken(userDTO);
//
//            // Create LoginResponse
//            LoginResponse loginResponse = new LoginResponse(userDTO, token);
//
//            return ResponseEntity.ok(
//                    DataResponse.builder()
//                            .success(true)
//                            .message("Login successful")
//                            .data(loginResponse)
//                            .build()
//            );
//        } catch (InvalidCredentialsException e) {
//            // Handle invalid credentials
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    DataResponse.builder()
//                            .success(false)
//                            .message("Invalid credentials")
//                            .build()
//            );
//        } catch (Exception e) {
//            // Handle unexpected errors
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    DataResponse.builder()
//                            .success(false)
//                            .message("An unexpected error occurred.")
//                            .build()
//            );
//        }
//    }
}
