package com.beaconfire.posts_service.domain;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerationInfo {

    private boolean isPinned;
    private boolean isLocked;
    private int reportCount;
    private String moderatorNotes;


}
