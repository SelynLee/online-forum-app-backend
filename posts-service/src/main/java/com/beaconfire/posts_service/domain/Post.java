package com.beaconfire.posts_service.domain;
import java.util.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.service.invoker.HttpRequestValues.Metadata;

import lombok.*;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post {
	@Id
	private String postId;
    private String title;
    private String content;
    private boolean isArchived;
    private Author author;
    private String visibility;
    private String status; 
    private Metadata metadata;
    private Date created_at;
    private Date updated_at;
    private List<String> images;
    private List<String> attachments;
    private List<PostReply> postReplies;
    private ModerationInfo moderationInfo;
}
