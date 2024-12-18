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
    	return Accessibility.valueOf(value.toUpperCase());

   
}
    }
