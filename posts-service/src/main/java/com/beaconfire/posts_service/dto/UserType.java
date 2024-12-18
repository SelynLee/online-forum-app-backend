package com.beaconfire.posts_service.dto;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserType {
    VISITOR, NORMAL, ADMIN, SUPERADMIN;

    @JsonCreator
    public static UserType fromValue(String value) {

            return UserType.valueOf(value.toUpperCase());

   
}
}

