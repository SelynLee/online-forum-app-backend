package com.beaconfire.auth_service.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserType {
    VISITOR,
    NORMAL,
    ADMIN,
    SUPERADMIN
}