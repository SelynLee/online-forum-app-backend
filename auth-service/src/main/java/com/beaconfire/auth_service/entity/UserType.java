package com.beaconfire.auth_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserType {
    VISITOR, NORMAL, ADMIN, SUPERADMIN;

    @JsonCreator
    public static UserType fromValue(String value) {

            return UserType.valueOf(value.toUpperCase());

   
}
}