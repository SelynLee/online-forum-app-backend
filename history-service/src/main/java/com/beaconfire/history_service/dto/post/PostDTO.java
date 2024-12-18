package com.beaconfire.history_service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private String postId;
    private String title;
    private String content;
    private Integer userId;
    private String accessibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
