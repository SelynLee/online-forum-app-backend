package com.beaconfire.users_service.dto;

import java.time.LocalDateTime;

import com.beaconfire.users_service.domain.User;
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
public class UserDTO {

    private Integer id;
    private String email;
    private User.UserType type; 
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String profileImageUrl;


    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .type(user.getType())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
