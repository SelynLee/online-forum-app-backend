package com.beaconfire.history_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import com.beaconfire.history_service.dto.DataResponse;
import com.beaconfire.history_service.feign.PostFeignClient;
import com.beaconfire.history_service.feign.UserFeignClient;
import com.beaconfire.history_service.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserFeignClient userFeignClient;
    private final PostFeignClient postFeignClient;
    private final ObjectMapper objectMapper;

    public void validateUserAndPost(Integer userId, String postId) {
        try {
            DataResponse userResponse = userFeignClient.getUserById(userId);
            if (!userResponse.isSuccess()) {
                throw new ValidationException("User not found with ID: " + userId);
            }

        } catch (Exception e) {
            throw new ValidationException("Failed to validate user: " + e.getMessage());
        }

        try {
            DataResponse postResponse = postFeignClient.getPostById(postId);
            if (!postResponse.isSuccess()) {
                throw new ValidationException("Post not found with ID: " + postId);
            }

        } catch (Exception e) {
            throw new ValidationException("Failed to validate post: " + e.getMessage());
        }

    }
}