package com.beaconfire.posts_service.domain;

import java.util.Date;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Author {
	private String userId;
    private String username;
    private String profilePicture;
}
