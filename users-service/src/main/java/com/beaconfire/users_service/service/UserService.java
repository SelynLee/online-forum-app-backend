package com.beaconfire.users_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.domain.User.UserType;
import com.beaconfire.users_service.dto.UpdateDto;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.dto.UserPermissionsDTO;
import com.beaconfire.users_service.exception.ResourceNotFoundException;
import com.beaconfire.users_service.repo.UserRepo;

@Service
public class UserService {

    private final UserRepo userRepo;


    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;

    }

    // Find user by ID
    public UserDTO findUserById(int userId) {
        User user = userRepo.findById(userId)
                           .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return UserDTO.fromUser(user);
    }
    public UserPermissionsDTO getUserPermissions(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Assign permissions based on the user role
        boolean canCreatePost = false;
        boolean canDeleteReplies = false;
        boolean canBanUsers = false;
        boolean canModifyPosts = false;

        UserType role = user.getType();

        switch (role) {
            case SUPERADMIN:
                canCreatePost = true;
                canDeleteReplies = true;
                canBanUsers = true;
                canModifyPosts = true;
                break;
            case ADMIN:
                canCreatePost = true;
                canDeleteReplies = true;
                canBanUsers = true;
                canModifyPosts = false;
                break;
            case NORMAL:
                canCreatePost = true;
                canDeleteReplies = false;
                canBanUsers = false;
                canModifyPosts = true;
                break;
            case VISITOR:
            default:
                canCreatePost = false;
                canDeleteReplies = false;
                canBanUsers = false;
                canModifyPosts = false;
                break;
        }

        return UserPermissionsDTO.builder()
                .userId(user.getId())
                .role(role)
                .active(user.getActive())
                .canCreatePost(canCreatePost)
                .canDeleteReplies(canDeleteReplies)
                .canBanUsers(canBanUsers)
                .canModifyPosts(canModifyPosts)
                .build();
    }
    

    public UserDTO updateUserProfile(int userId, UpdateDto updateDto) {
        // Fetch the user from the repository
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update only the allowed fields
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(updateDto.getProfileImageUrl());
        }

        // Save the updated user
        User updatedUser = userRepo.save(user);

        // Convert and return the updated user as UserDTO
        return UserDTO.fromUser(updatedUser);
    }
    
    public UserDTO updateUserStatus(Integer userId, UserDTO userDTO, Integer currentUserId) {
        // Fetch the current user to verify Admin permissions
        User currentUser = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found."));

        if (!(currentUser.getType() == UserType.ADMIN || currentUser.getType() == UserType.SUPERADMIN)) {
            throw new RuntimeException("Forbidden: Only Admin users can perform this action.");
        }

        // Fetch the user to be updated
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update only 'active' and 'type' fields
        if (userDTO.getActive() != null) {
            user.setActive(userDTO.getActive());
        }
        if (userDTO.getType() != null) {
            user.setType(userDTO.getType());
        }

        User updatedUser = userRepo.save(user);

        // Convert to DTO and return
        return UserDTO.fromUser(updatedUser);
    }
    
    public List<UserDTO> getAllUsers() {
        // Fetch all users from the repository
        List<User> users = userRepo.findAll();
        return users.stream()
                    .map(UserDTO::fromUser) // Convert each User to UserDTO
                    .collect(Collectors.toList());
    }
    public UserDTO findUserByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserDTO.fromUser(user);
    }

    



}
