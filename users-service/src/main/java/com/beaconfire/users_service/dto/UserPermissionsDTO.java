package com.beaconfire.users_service.dto;

import com.beaconfire.users_service.domain.User.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionsDTO {
    private Integer userId;
    private UserType role; // VISITOR, NORMAL, ADMIN, SUPERADMIN
    private Boolean active;
    private Boolean canCreatePost;
    private Boolean canDeleteReplies;
    private Boolean canBanUsers;
    private Boolean canModifyPosts;
}
