package com.beaconfire.history_service.service;

import org.springframework.stereotype.Service;

import com.beaconfire.history_service.dto.DataResponse;
import com.beaconfire.history_service.feign.UserFeignClient;
import com.beaconfire.history_service.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserFeignClient userFeignClient;

    public void validateUserAndPost(Integer userId, String postId) {
        try {
            DataResponse userResponse = userFeignClient.getUserById(userId);
            if (!userResponse.isSuccess()) {
                throw new ValidationException("User not found with ID: " + userId);
            }

        } catch (Exception e) {
            throw new ValidationException("Failed to validate user: " + e.getMessage());
        }

    }
}