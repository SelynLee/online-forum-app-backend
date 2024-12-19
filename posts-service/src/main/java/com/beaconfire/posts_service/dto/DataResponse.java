package com.beaconfire.posts_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DataResponse {
    private Boolean success;
    private String message;
    private Object data;
}


