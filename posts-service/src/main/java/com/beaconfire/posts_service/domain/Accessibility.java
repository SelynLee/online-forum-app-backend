package com.beaconfire.posts_service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Accessibility {
    UNPUBLISHED,
    PUBLISHED,
    HIDDEN,
    BANNED,
    DELETED;

    @JsonCreator
    public static Accessibility fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Accessibility value cannot be null");
        }
        try {
            return Accessibility.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid Accessibility value: " + value);
        }
    }
}
