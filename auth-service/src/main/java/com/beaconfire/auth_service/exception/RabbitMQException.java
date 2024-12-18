package com.beaconfire.auth_service.exception;

public class RabbitMQException extends RuntimeException {
    public RabbitMQException(String message) {
        super(message);
    }
}
