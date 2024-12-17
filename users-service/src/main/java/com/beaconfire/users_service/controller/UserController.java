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

}
