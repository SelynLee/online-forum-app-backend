package com.beaconfire.posts_service.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post {

    @Id
    private String postId;

    @NotEmpty(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotEmpty(message = "Content is required")
    private String content;

    private boolean isArchived;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotEmpty(message = "Visibility is required")
    private String visibility;

    @NotEmpty(message = "Status is required")
    private String status;

    private Metadata metadata;

    private Date created_at;

    private Date updated_at;

    private List<String> images;

    private List<String> attachments;

    private List<PostReply> postReplies;

    private ModerationInfo moderationInfo;
}
