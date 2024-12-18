package com.beaconfire.history_service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponseDTO {
    private Integer historyId;
    private Integer userId;
    private String postId;
    private LocalDateTime viewDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}