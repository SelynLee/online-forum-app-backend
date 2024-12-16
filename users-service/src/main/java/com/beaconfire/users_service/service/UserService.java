package com.beaconfire.users_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beaconfire.users_service.InvalidCredentialsException;
import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.repo.UserRepo;

@Service
@Transactional
public class UserService {

    private final UserRepo userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepo userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    // Register a new user
    public UserDTO registerUser(User user) {
        userDao.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already exists");
        });
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.save(user);
        return UserDTO.fromUser(user);
    }

    // Authenticate a user by email and password
    public UserDTO authenticateUser(String email, String password) throws InvalidCredentialsException {
        User user = userDao.findByEmail(email)
                           .orElseThrow(() -> new InvalidCredentialsException("Incorrect credentials, please try again."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect credentials, please try again.");
        }

        return UserDTO.fromUser(user);
    }

    // Find user by ID
    public UserDTO findUserById(int userId) {
        User user = userDao.findById(userId)
                           .orElseThrow(() -> new InvalidCredentialsException("User not found with ID: " + userId));
        return UserDTO.fromUser(user);
    }
}
