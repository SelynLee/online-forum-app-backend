package com.beaconfire.users_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beaconfire.users_service.dto.DataResponse;
import com.beaconfire.users_service.dto.UpdateDto;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.dto.UserPermissionsDTO;
import com.beaconfire.users_service.exception.ResourceNotFoundException;
import com.beaconfire.users_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@Tag(name = "User Management", description = "APIs for managing user data") // Grouping for Swagger
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Fetch user by ID
    @Operation(
    	    summary = "Get User by ID",
    	    description = "Retrieve the details of a user by their unique ID."
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "User fetched successfully"),
    	    @ApiResponse(responseCode = "400", description = "Invalid user ID"),
    	    @ApiResponse(responseCode = "404", description = "User not found"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    	@GetMapping("/{id}")
    	public ResponseEntity<DataResponse> getUserById(
    	        @Parameter(description = "ID of the user to be fetched", required = true) 
    	        @PathVariable("id") Integer userId) {
    	    try {
    	        // Validate user ID
    	        if (userId == null || userId <= 0) {
    	            return ResponseEntity.badRequest().body(
    	                    DataResponse.builder()
    	                            .success(false)
    	                            .message("Invalid user ID. ID must be greater than 0.")
    	                            .data(null)
    	                            .build()
    	            );
    	        }

    	        UserDTO userDTO = userService.findUserById(userId);
    	        return ResponseEntity.ok(
    	                DataResponse.builder()
    	                        .success(true)
    	                        .message("User fetched successfully")
    	                        .data(userDTO)
    	                        .build()
    	        );
    	    } catch (ResourceNotFoundException e) {
    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
    	                DataResponse.builder()
    	                        .success(false)
    	                        .message(e.getMessage())
    	                        .data(null)
    	                        .build()
    	        );
    	    } catch (Exception e) {
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
    	                DataResponse.builder()
    	                        .success(false)
    	                        .message("An unexpected error occurred.")
    	                        .data(null)
    	                        .build()
    	        );
    	    }
    	}


    @Operation(
    	    summary = "Update User Profile",
    	    description = "Partially update a user's profile details using a PATCH request."
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
    	    @ApiResponse(responseCode = "404", description = "User not found"),
    	    @ApiResponse(responseCode = "400", description = "Invalid input"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    	@PatchMapping("/{id}")
    	public ResponseEntity<DataResponse> updateUserProfile(
    	        @Parameter(description = "ID of the user to update", required = true)
    	        @PathVariable("id") Integer userId,

    	        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	                description = "User profile data to be updated",
    	                required = true
    	        )
    	        @Valid @RequestBody UpdateDto updateDto) {
    	    try {
    	        UserDTO updatedUser = userService.updateUserProfile(userId, updateDto);

    	        return ResponseEntity.ok(
    	                DataResponse.builder()
    	                        .success(true)
    	                        .message("User profile updated successfully.")
    	                        .data(updatedUser)
    	                        .build()
    	        );
    	    } catch (ResourceNotFoundException e) {
    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
    	                DataResponse.builder()
    	                        .success(false)
    	                        .message(e.getMessage())
    	                        .data(null)
    	                        .build()
    	        );
    	    } catch (Exception e) {
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
    	                DataResponse.builder()
    	                        .success(false)
    	                        .message("An unexpected error occurred.")
    	                        .data(null)
    	                        .build()
    	        );
    	    }
    	}



    
    @Operation(summary = "Get user permissions", description = "Fetches the permissions for a user based on their role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User permissions fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/permissions")
    public ResponseEntity<DataResponse> getUserPermissions(@PathVariable("id") Integer userId) {
        try {
            UserPermissionsDTO permissions = userService.getUserPermissions(userId);
            return ResponseEntity.ok(
                    DataResponse.builder()
                            .success(true)
                            .message("User permissions fetched successfully")
                            .data(permissions)
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(
                    DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    DataResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred.")
                            .data(null)
                            .build()
            );
        }
    }
    
    @Operation(
    	    summary = "Update User Status (Ban/Unban or Change Type)",
    	    description = "Allows Admins to ban/unban users by updating their 'active' status or change their user type."
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "User status updated successfully"),
    	    @ApiResponse(responseCode = "404", description = "User not found"),
    	    @ApiResponse(responseCode = "403", description = "Forbidden - Only Admins can perform this action"),
    	    @ApiResponse(responseCode = "400", description = "Invalid input"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<DataResponse> updateUserStatus(
            @PathVariable("id") Integer userId,
            @RequestBody @Valid UserDTO userDTO,
            @RequestParam("currentUserId") Integer currentUserId
    ) {
        try {
            UserDTO updatedUser = userService.updateUserStatus(userId, userDTO, currentUserId);
            return ResponseEntity.ok(
                    DataResponse.builder()
                            .success(true)
                            .message("User status updated successfully.")
                            .data(updatedUser)
                            .build()
            );
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    DataResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    DataResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred.")
                            .data(null)
                            .build()
            );
        }
    }

    


    
    @Operation(
    	    summary = "Get All Users",
    	    description = "Fetch all users from the system as a list of user details."
    	)
    	@ApiResponses({
    	    @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    	@GetMapping
    	public ResponseEntity<DataResponse> getAllUsers() {
    	    try {
    	        // Call the service to fetch all users
    	        List<UserDTO> users = userService.getAllUsers();

    	        // Return success response
    	        return ResponseEntity.ok(
    	                DataResponse.builder()
    	                        .success(true)
    	                        .message("Users fetched successfully.")
    	                        .data(users)
    	                        .build()
    	        );
    	    } catch (Exception e) {
    	        // Handle unexpected errors
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
    	                DataResponse.builder()
    	                        .success(false)
    	                        .message("An unexpected error occurred.")
    	                        .data(null)
    	                        .build()
    	        );
    	    }
    	}
    
    @Operation(
            summary = "Get User by Email",
            description = "Retrieve the details of a user by their email address."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User fetched successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid email input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/email")
        public ResponseEntity<DataResponse> getUserByEmail(
                @Parameter(description = "Email of the user to be fetched", required = true)
                @RequestParam("email") String email) {
            try {
                UserDTO userDTO = userService.findUserByEmail(email);
                return ResponseEntity.ok(
                        DataResponse.builder()
                                .success(true)
                                .message("User fetched successfully.")
                                .data(userDTO)
                                .build()
                );
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        DataResponse.builder()
                                .success(false)
                                .message(e.getMessage())
                                .data(null)
                                .build()
                );
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        DataResponse.builder()
                                .success(false)
                                .message("Invalid email format.")
                                .data(null)
                                .build()
                );
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        DataResponse.builder()
                                .success(false)
                                .message("An unexpected error occurred.")
                                .data(null)
                                .build()
                );
            }
        }
    
    
    


}
