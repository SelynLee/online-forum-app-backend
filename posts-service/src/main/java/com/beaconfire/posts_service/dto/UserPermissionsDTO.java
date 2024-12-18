package com.beaconfire.posts_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsDTO {
    private Integer userId;
    private UserType type;   // VISITOR, NORMAL, ADMIN, SUPERADMIN
    private Boolean active;  // Account active status
    private Boolean canCreatePost; // Email verification status
    private Boolean canDeleteReplies;
    private Boolean canBanUsers;
    private Boolean canModifyPosts;

}
