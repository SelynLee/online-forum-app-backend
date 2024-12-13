package com.beaconfire.posts_service.domain;

import java.util.Date;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {

    private int views;
    private int likes;
    private int totalReplies;
    private int bookmarks;
    private Date createdAt;
    private Date updatedAt;
    private Date lastActivityAt;


}

