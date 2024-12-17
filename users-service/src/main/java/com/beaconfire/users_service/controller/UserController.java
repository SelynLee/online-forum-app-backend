package com.beaconfire.users_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.beaconfire.users_service.dto.DataResponse;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.exception.ResourceNotFoundException;
//import com.beaconfire.users_service.security.JwtProvider;
import com.beaconfire.users_service.service.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

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
        } catch (ResourceNotFoundException e) {
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
    @PatchMapping("/users/{id}")
    public ResponseEntity<DataResponse> updateUserProfile(
            @PathVariable("id") Integer userId,
            @Valid @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUserProfile(userId, userDTO);
            return ResponseEntity.ok(
                    DataResponse.builder()
                            .success(true)
                            .message("User profile updated successfully")
                            .data(updatedUser)
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DataResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred.")
                            .data(null)
                            .build());
        }
    }
}


