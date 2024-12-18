package com.beaconfire.posts_service.dto;

import com.beaconfire.posts_service.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostWithUserDTO {
    private Post post;   
    private UserDTO user;
}

