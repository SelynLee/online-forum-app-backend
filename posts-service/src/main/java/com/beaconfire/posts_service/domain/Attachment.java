package com.beaconfire.posts_service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Attachment {
    private String type; 
    private String url;
    private String filename;
    private long size;
    private String mimeType;
}
