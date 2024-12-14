package com.beaconfire.posts_service.controller;

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

import com.beaconfire.posts_service.domain.Metadata;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.dto.DataResponse;
import com.beaconfire.posts_service.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {

   
    private final PostService postService;
    public PostController(PostService postService) {
    	this.postService=postService;
    }

    @PostMapping
    public DataResponse createPost(@Valid @RequestBody Post post, BindingResult result) {
        if (result.hasErrors()) {
            // Collect error messages
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

        // Proceed with saving the post if validation passes
        Post createdPost = postService.createPost(post);
        return DataResponse.builder()
                .success(true)
                .message("Post created successfully")
                .data(createdPost)
                .build();
    }


    @PutMapping("/{postId}")
    public DataResponse updatePost(@PathVariable String postId, @Valid @RequestBody Post updatedPost, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            // Collect error messages
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

        // Proceed with updating the post if validation passes
        Post post = postService.updatePost(postId, updatedPost);
        return DataResponse.builder()
                .success(true)
                .message("Post updated successfully")
                .data(post)
                .build();
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

    @GetMapping("/status/{status}")
    public DataResponse getPostsByStatus(@PathVariable String status) {
        List<Post> posts = postService.getPostsByStatus(status);
        return DataResponse.builder()
                .success(true)
                .message("Posts with status: " + status + " retrieved successfully")
                .data(posts)
                .build();
    }

    @GetMapping("/{postId}")
    public DataResponse getPostById(@PathVariable String postId) {
        Post post = postService.getPostById(postId);
        return DataResponse.builder()
                .success(true)
                .message("Post retrieved successfully")
                .data(post)
                .build();
    }
    
    @PatchMapping("/{postId}/metadata")
    public DataResponse updateMetadata(@PathVariable String postId, @RequestBody Metadata metadata) {
        Post updatedPost = postService.updateMetadata(postId, metadata);
        return DataResponse.builder()
                .success(true)
                .message("Metadata updated successfully")
                .data(updatedPost)
                .build();
    }

}
