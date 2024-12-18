package com.beaconfire.history_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryCreateDTO {
    @NotNull(message = "User ID cannot be null")
    private Integer userId;

    @NotNull(message = "Post ID cannot be null")
    private String postId;
}
