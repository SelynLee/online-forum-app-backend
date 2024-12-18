package com.beaconfire.auth_service.exception;

public class UserIsNotActive extends RuntimeException {
    public UserIsNotActive(String message) {
        super(message);
    }
}
