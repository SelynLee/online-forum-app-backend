package com.beaconfire.posts_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessibilityRequest {
    private String accessibility;
    private Integer currentUserId;
}


