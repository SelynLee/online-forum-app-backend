package com.beaconfire.users_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.dto.UserDTO;
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

    public UserDTO updateUserProfile(int userId, UserDTO userDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update fields if provided
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setProfileImageUrl(userDTO.getProfileImageUrl());
        user.setActive(userDTO.getActive());
        user.setType(userDTO.getType());

        // Save updated user
        User updatedUser = userRepo.save(user);

        return UserDTO.fromUser(updatedUser);
    }
}
