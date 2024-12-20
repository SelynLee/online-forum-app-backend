package com.beaconfire.posts_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.beaconfire.posts_service.domain.Metadata;
import com.beaconfire.posts_service.domain.Post;
import com.beaconfire.posts_service.dto.DataResponse;
import com.beaconfire.posts_service.dto.PostWithUserDTO;
import com.beaconfire.posts_service.dto.UserDTO;
import com.beaconfire.posts_service.dto.UserPermissionsDTO;
import com.beaconfire.posts_service.feign.UserFeignClient;
import com.beaconfire.posts_service.repo.PostRepository;
import com.beaconfire.posts_service.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserFeignClient userFeignClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPostsByUserId_UserExists_ReturnsPostsWithUser() {
        // Arrange
        Integer userId = 1;
        Post post = new Post();
        post.setPostId("post1");
        post.setUserId(userId);
        List<Post> posts = List.of(post);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userId);
        userDTO.setFirstName("John");

        DataResponse userResponse = mock(DataResponse.class);
        when(userResponse.getSuccess()).thenReturn(true);
        when(userResponse.getData()).thenReturn(userDTO);

        when(postRepository.findByUserId(userId)).thenReturn(posts);
        when(userFeignClient.getUserById(userId)).thenReturn(userResponse);
        when(objectMapper.convertValue(userResponse.getData(), UserDTO.class)).thenReturn(userDTO);

        // Act
        List<PostWithUserDTO> result = postService.getPostsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("post1", result.get(0).getPost().getPostId());
        assertEquals("John", result.get(0).getUser().getFirstName());
        verify(postRepository, times(1)).findByUserId(userId);
        verify(userFeignClient, times(1)).getUserById(userId);
    }

    @Test
    void createPost_ValidUser_CreatesPost() {
        // Arrange
        Integer userId = 1;
        Post post = new Post();
        post.setUserId(userId);

        UserPermissionsDTO permissions = new UserPermissionsDTO();
        permissions.setActive(true);

        DataResponse mockResponse = mock(DataResponse.class);
        when(mockResponse.getSuccess()).thenReturn(true);
        when(mockResponse.getData()).thenReturn(permissions);

        when(userFeignClient.getUserById(userId)).thenReturn(mockResponse);
        when(objectMapper.convertValue(mockResponse.getData(), UserPermissionsDTO.class)).thenReturn(permissions);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.createPost(post);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPostId());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void createPost_InactiveUser_ThrowsException() {
        // Arrange
        Integer userId = 1;
        Post post = new Post();
        post.setUserId(userId);

        UserPermissionsDTO permissions = new UserPermissionsDTO();
        permissions.setActive(false);

        DataResponse mockResponse = mock(DataResponse.class);
        when(mockResponse.getSuccess()).thenReturn(true);
        when(mockResponse.getData()).thenReturn(permissions);

        when(userFeignClient.getUserById(userId)).thenReturn(mockResponse);
        when(objectMapper.convertValue(mockResponse.getData(), UserPermissionsDTO.class)).thenReturn(permissions);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> postService.createPost(post));
        assertEquals("User must verify their email to create a post.", exception.getMessage());
        verify(postRepository, never()).save(post);
    }

    @Test
    void likePost_PostExists_LikesPost() {
        // Arrange
        String postId = "post1";
        Integer userId = 1;

        Post post = new Post();
        Metadata metadata = new Metadata();
        metadata.setLikes(0);
        metadata.setLikesByUsers(new HashSet<>());

        post.setMetadata(metadata);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.likePost(postId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMetadata().getLikes());
        assertTrue(result.getMetadata().getLikesByUsers().contains(userId));
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void unlikePost_PostExists_UnlikesPost() {
        // Arrange
        String postId = "post1";
        Integer userId = 1;

        Post post = new Post();
        Metadata metadata = new Metadata();
        metadata.setLikes(1);
        metadata.setLikesByUsers(new HashSet<>(Set.of(userId)));

        post.setMetadata(metadata);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.unlikePost(postId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getMetadata().getLikes());
        assertFalse(result.getMetadata().getLikesByUsers().contains(userId));
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void incrementViews_PostExists_IncrementsViewCount() {
        // Arrange
        String postId = "post1";

        Post post = new Post();
        Metadata metadata = new Metadata();
        metadata.setViews(5);
        post.setMetadata(metadata);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.incrementViews(postId);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.getMetadata().getViews());
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(post);
    }
}
