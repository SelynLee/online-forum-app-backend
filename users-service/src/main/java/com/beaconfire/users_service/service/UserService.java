package com.beaconfire.users_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.exception.InvalidCredentialsException;
import com.beaconfire.users_service.repo.UserRepo;

@Service
public class UserService {

    private final UserRepo userDao;


    @Autowired
    public UserService(UserRepo userDao) {
        this.userDao = userDao;

    }

    // Find user by ID
    public UserDTO findUserById(int userId) {
        User user = userDao.findById(userId)
                           .orElseThrow(() -> new InvalidCredentialsException("User not found with ID: " + userId));
        return UserDTO.fromUser(user);
    }
}
