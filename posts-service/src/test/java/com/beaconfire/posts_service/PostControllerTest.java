package com.beaconfire.posts_service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import com.beaconfire.posts_service.controller.PostController;
import com.beaconfire.posts_service.domain.Accessibility;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.dto.AccessibilityRequest;
import com.beaconfire.posts_service.dto.DataResponse;
import com.beaconfire.posts_service.dto.PostWithUserDTO;
import com.beaconfire.posts_service.dto.UserDTO;
import com.beaconfire.posts_service.service.PostService;

class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPost_Success() {
        // Arrange
        Post post = new Post();
        post.setPostId("1");
        post.setTitle("Test Post");
        post.setContent("Test content");
        post.setUserId(1);
        post.setAccessibility(Accessibility.PUBLISHED);

        when(postService.createPost(post)).thenReturn(post);

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false); // Simulate no validation errors

        // Act
        DataResponse response = postController.createPost(post, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Post created successfully", response.getMessage());
        assertEquals(post, response.getData());
        verify(postService, times(1)).createPost(post);
    }

    @Test
    void updatePost_Success() {
        // Arrange
        Post updatedPost = new Post();
        updatedPost.setPostId("1");
        updatedPost.setTitle("Updated Post");
        updatedPost.setContent("Updated content");
        updatedPost.setUserId(1);
        updatedPost.setAccessibility(Accessibility.PUBLISHED);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setEmail("user@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        PostWithUserDTO updatedPostWithUserDTO = PostWithUserDTO.builder()
                .post(updatedPost)
                .user(userDTO)
                .build();

        when(postService.updatePost("1", updatedPost)).thenReturn(updatedPostWithUserDTO);

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false); // Simulate no validation errors

        // Act
        DataResponse response = postController.updatePost("1", updatedPost, bindingResult);

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Post updated successfully", response.getMessage());
        assertEquals(updatedPostWithUserDTO, response.getData());
        verify(postService, times(1)).updatePost("1", updatedPost);
    }


    @Test
    void deletePost_Success() {
        // Act
        DataResponse response = postController.deletePost("1");

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Post deleted successfully", response.getMessage());
        verify(postService, times(1)).deletePost("1");
    }

    @Test
    void getAllPosts_Success() {
        // Arrange
        PostWithUserDTO postWithUserDTO = new PostWithUserDTO();
        postWithUserDTO.setPost(new Post());

        when(postService.getAllPosts()).thenReturn(List.of(postWithUserDTO));

        // Act
        DataResponse response = postController.getAllPosts();

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Posts retrieved successfully", response.getMessage());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void getPostsByUserId_Success() {
        // Arrange
        PostWithUserDTO postWithUserDTO = new PostWithUserDTO();
        postWithUserDTO.setPost(new Post());

        when(postService.getPostsByUserId(1)).thenReturn(List.of(postWithUserDTO));

        // Act
        DataResponse response = postController.getPostsWithUserByUserId(1);

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Posts fetched successfully for user ID: 1", response.getMessage());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(postService, times(1)).getPostsByUserId(1);
    }

    @Test
    void updateAccessibility_Success() {
        // Arrange
        Post updatedPost = new Post();
        updatedPost.setPostId("1");
        updatedPost.setAccessibility(Accessibility.PUBLISHED);

        AccessibilityRequest request = AccessibilityRequest.builder()
                .accessibility("PUBLISHED")
                .currentUserId(1)
                .build();

        when(postService.updateAccessibility("1", Accessibility.PUBLISHED, 1)).thenReturn(updatedPost);

        // Act
        DataResponse response = postController.updateAccessibility("1", request);

        // Assert
        assertNotNull(response);
        assertEquals(true, response.getSuccess());
        assertEquals("Accessibility updated successfully.", response.getMessage());
        assertEquals(updatedPost, response.getData());
        verify(postService, times(1)).updateAccessibility("1", Accessibility.PUBLISHED, 1);
    }
}

