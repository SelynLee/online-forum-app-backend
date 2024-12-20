package com.beaconfire.users_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DataResponse {
    private Boolean success;
    private String message;
    private Object data;
}

