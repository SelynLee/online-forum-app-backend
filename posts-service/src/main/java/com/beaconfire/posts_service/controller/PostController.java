package com.beaconfire.posts_service.controller;

import java.util.EnumSet;
import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beaconfire.posts_service.domain.Accessibility;
import com.beaconfire.posts_service.domain.Metadata;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.domain.PostReply;
import com.beaconfire.posts_service.domain.SubReply;
import com.beaconfire.posts_service.dto.DataResponse;
import com.beaconfire.posts_service.exception.InvalidAccessibilityException;
import com.beaconfire.posts_service.exception.PostNotFoundException;
import com.beaconfire.posts_service.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {

   
    private final PostService postService;
    public PostController(PostService postService) {
    	this.postService=postService;
    }
    private void validateAccessibility(Accessibility accessibility) {
        if (accessibility == null) {
            throw new InvalidAccessibilityException("Accessibility must be provided and cannot be null.");
        }


        boolean isValid = EnumSet.allOf(Accessibility.class).contains(accessibility);
        if (!isValid) {
            throw new InvalidAccessibilityException("Invalid accessibility value: " + accessibility);
        }
    }

    @PostMapping
    public DataResponse createPost(@Valid @RequestBody Post post, BindingResult result) {
        if (result.hasErrors()) {
            // Collect validation error messages
            String errorMessage = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation failed");

            // Return validation error response
            return DataResponse.builder()
                    .success(false)
                    .message(errorMessage)
                    .data(null)
                    .build();
        }

        try {
            // Validate accessibility
            validateAccessibility(post.getAccessibility());

            // Save the post
            Post createdPost = postService.createPost(post);
            return DataResponse.builder()
                    .success(true)
                    .message("Post created successfully")
                    .data(createdPost)
                    .build();
        } catch (InvalidAccessibilityException ex) {
            return DataResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            return DataResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    @PutMapping("/{postId}")
    public DataResponse updatePost(@PathVariable String postId, @Valid @RequestBody Post updatedPost, BindingResult result) {
        if (result.hasErrors()) {
            // Collect validation error messages
            String errorMessage = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation failed");

            // Return validation error response
            return DataResponse.builder()
                    .success(false)
                    .message(errorMessage)
                    .data(null)
                    .build();
        }

        try {
            // Validate accessibility
            validateAccessibility(updatedPost.getAccessibility());

            // Update the post
            Post updatedPostEntity = postService.updatePost(postId, updatedPost);
            return DataResponse.builder()
                    .success(true)
                    .message("Post updated successfully")
                    .data(updatedPostEntity)
                    .build();
        } catch (PostNotFoundException ex) {
            return DataResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (InvalidAccessibilityException ex) {
            return DataResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            return DataResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }


    @DeleteMapping("/{postId}")
    public DataResponse deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return DataResponse.builder()
                .success(true)
                .message("Post deleted successfully")
                .data(null)
                .build();
    }

    @GetMapping
    public DataResponse getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return DataResponse.builder()
                .success(true)
                .message("Posts retrieved successfully")
                .data(posts)
                .build();
    }

//    @GetMapping("/status/{status}")
//    public DataResponse getPostsByStatus(@PathVariable String status) {
//        List<Post> posts = postService.getPostsByStatus(status);
//        return DataResponse.builder()
//                .success(true)
//                .message("Posts with status: " + status + " retrieved successfully")
//                .data(posts)
//                .build();
//    }

    @GetMapping("/{postId}")
    public DataResponse getPostById(@PathVariable String postId) {
        try {
            // Attempt to retrieve the post
            Post post = postService.getPostById(postId);
            return DataResponse.builder()
                    .success(true)
                    .message("Post retrieved successfully")
                    .data(post)
                    .build();
        } catch (PostNotFoundException ex) {
            // Handle case where the post is not found
            return DataResponse.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build();
        } catch (Exception ex) {
            // Handle any unexpected errors
            return DataResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred: " + ex.getMessage())
                    .data(null)
                    .build();
        }
    }

    
    @PatchMapping("/{postId}/metadata")
    public DataResponse updateMetadata(@PathVariable String postId, @RequestBody Metadata metadata) {
    	try {
    	Post updatedPost = postService.updateMetadata(postId, metadata);
    	return DataResponse.builder()
                .success(true)
                .message("Metadata updated successfully")
                .data(updatedPost)
                .build();
    } catch (PostNotFoundException ex) {
        // Handle case where the post is not found
        return DataResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
    } catch (Exception ex) {
        // Handle any unexpected errors
        return DataResponse.builder()
                .success(false)
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(null)
                .build();
    }
}
    
    @PostMapping("/{postId}/replies")
    public DataResponse addReplyToPost(@PathVariable String postId, @RequestBody PostReply comment) {
    	try {
    	Post updatedPost = postService.addReplyToPost(postId, comment);
    	return DataResponse.builder()
                .success(true)
                .message("Replied successfully")
                .data(updatedPost)
                .build();
    } catch (PostNotFoundException ex) {
        // Handle case where the post is not found
        return DataResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
    } catch (Exception ex) {
        // Handle any unexpected errors
        return DataResponse.builder()
                .success(false)
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(null)
                .build();
    }
  }
    
    
    @PostMapping("/{postId}/replies/{replyId}/subreplies")
    public DataResponse addSubReplyToReply(@PathVariable String postId, @PathVariable String replyId, @RequestBody SubReply subReply) {
    	try {
    	Post updatedPost = postService.addSubReplyToReply(postId, replyId, subReply);
    	return DataResponse.builder()
                .success(true)
                .message("Replied successfully")
                .data(updatedPost)
                .build();
    } catch (PostNotFoundException ex) {
        // Handle case where the post is not found
        return DataResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();
    } catch (Exception ex) {
        // Handle any unexpected errors
        return DataResponse.builder()
                .success(false)
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(null)
                .build();
    }
  }



}
