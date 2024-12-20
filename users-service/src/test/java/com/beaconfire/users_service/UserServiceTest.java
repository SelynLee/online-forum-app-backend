package com.beaconfire.users_service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.domain.User.UserType;
import com.beaconfire.users_service.dto.UpdateDto;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.dto.UserPermissionsDTO;
import com.beaconfire.users_service.exception.ResourceNotFoundException;
import com.beaconfire.users_service.repo.UserRepo;
import com.beaconfire.users_service.service.UserService;

class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserById_UserExists() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = userService.findUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepo, times(1)).findById(1);
    }


    @Test
    void findUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findUserById(1)
        );
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    void getUserPermissions_UserExists_ReturnsCorrectPermissions() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setType(UserType.ADMIN);
        user.setActive(true);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissionsDTO = userService.getUserPermissions(1);

        // Assert
        assertNotNull(permissionsDTO);
        assertEquals(1, permissionsDTO.getUserId());
        assertTrue(permissionsDTO.getCanCreatePost());
        assertTrue(permissionsDTO.getCanDeleteReplies());
        assertTrue(permissionsDTO.getCanBanUsers());
        assertFalse(permissionsDTO.getCanModifyPosts());
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        User user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .profileImageUrl("old-image-url")
                .build();

        UpdateDto updateDto = UpdateDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .profileImageUrl("new-image-url")
                .build();

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("new-image-url", result.getProfileImageUrl());
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(user);
    }



    @Test
    void updateUserProfile_UserNotFound() {
        // Arrange
        UpdateDto updateDto = UpdateDto.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        when(userRepo.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.updateUserProfile(99, updateDto));

        assertEquals("User not found with ID: 99", exception.getMessage());
        verify(userRepo, times(1)).findById(99);
        verify(userRepo, never()).save(any(User.class));
    }



    @Test
    void updateUserProfile_PartialUpdate() {
        // Arrange
        User user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .profileImageUrl("old-image-url")
                .build();

        UpdateDto updateDto = UpdateDto.builder()
                .lastName("Smith")
                .profileImageUrl("new-image-url")
                .build(); // Only updating last name and profile image

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName()); // Unchanged
        assertEquals("Smith", result.getLastName()); // Updated
        assertEquals("john.doe@example.com", result.getEmail()); // Unchanged
        assertEquals("new-image-url", result.getProfileImageUrl()); // Updated
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(user);
    }
    @Test
    void getAllUsers_Success() {
        // Arrange
        User user1 = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .build();

        List<User> users = List.of(user1, user2);
        when(userRepo.findAll()).thenReturn(users);

        // Act
        List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Validate the first user
        UserDTO userDTO1 = result.get(0);
        assertEquals(1, userDTO1.getId());
        assertEquals("John", userDTO1.getFirstName());
        assertEquals("Doe", userDTO1.getLastName());
        assertEquals("john.doe@example.com", userDTO1.getEmail());

        // Validate the second user
        UserDTO userDTO2 = result.get(1);
        assertEquals(2, userDTO2.getId());
        assertEquals("Jane", userDTO2.getFirstName());
        assertEquals("Smith", userDTO2.getLastName());
        assertEquals("jane.smith@example.com", userDTO2.getEmail());

        verify(userRepo, times(1)).findAll();
    }
    
    @Test
    void findUserByEmail_UserFound() {
        // Arrange
        String email = "john.doe@example.com";
        User user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .build();

        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = userService.findUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(email, result.getEmail());
        verify(userRepo, times(1)).findByEmail(email);
    }
    @Test
    void findUserByEmail_UserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findUserByEmail(email));
        assertEquals("User not found with email: " + email, exception.getMessage());
        verify(userRepo, times(1)).findByEmail(email);
    }





    @Test
    void updateUserStatus_Success() {
        // Arrange
        User currentUser = User.builder()
                .id(100)
                .type(User.UserType.ADMIN)
                .build();

        User userToUpdate = User.builder()
                .id(1)
                .active(false)
                .type(User.UserType.NORMAL)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .active(true)
                .type(User.UserType.ADMIN)
                .build();

        when(userRepo.findById(100)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1)).thenReturn(Optional.of(userToUpdate));
        when(userRepo.save(any(User.class))).thenReturn(userToUpdate);

        // Act
        UserDTO result = userService.updateUserStatus(1, userDTO, 100);

        // Assert
        assertNotNull(result);
        assertEquals(true, result.getActive());
        assertEquals(User.UserType.ADMIN, result.getType());
        verify(userRepo, times(1)).findById(100);
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(userToUpdate);
    }

    @Test
    void updateUserStatus_CurrentUserNotFound() {
        // Arrange
        when(userRepo.findById(100)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserStatus(1, new UserDTO(), 100));

        assertEquals("Current user not found.", exception.getMessage());
        verify(userRepo, times(1)).findById(100);
    }

    @Test
    void updateUserStatus_CurrentUserForbidden() {
        // Arrange
        User currentUser = User.builder()
                .id(100)
                .type(User.UserType.NORMAL)
                .build();

        when(userRepo.findById(100)).thenReturn(Optional.of(currentUser));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.updateUserStatus(1, new UserDTO(), 100));

        assertEquals("Forbidden: Only Admin users can perform this action.", exception.getMessage());
        verify(userRepo, times(1)).findById(100);
    }

    @Test
    void updateUserStatus_UserToBeUpdatedNotFound() {
        // Arrange
        User currentUser = User.builder()
                .id(100)
                .type(User.UserType.ADMIN)
                .build();

        when(userRepo.findById(100)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUserStatus(1, new UserDTO(), 100));

        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepo, times(1)).findById(100);
        verify(userRepo, times(1)).findById(1);
    }

    @Test
    void updateUserStatus_NoFieldsToUpdate() {
        // Arrange
        User currentUser = User.builder()
                .id(100)
                .type(User.UserType.ADMIN)
                .build();

        User userToUpdate = User.builder()
                .id(1)
                .active(false)
                .type(User.UserType.NORMAL)
                .build();

        UserDTO userDTO = UserDTO.builder().build(); // No fields to update

        when(userRepo.findById(100)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1)).thenReturn(Optional.of(userToUpdate));
        when(userRepo.save(any(User.class))).thenReturn(userToUpdate);

        // Act
        UserDTO result = userService.updateUserStatus(1, userDTO, 100);

        // Assert
        assertNotNull(result);
        assertEquals(false, result.getActive());
        assertEquals(User.UserType.NORMAL, result.getType());
        verify(userRepo, times(1)).findById(100);
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(userToUpdate);
    }
    @Test
    void updateUserProfile_AllFieldsNull() {
        // Arrange
        User user = User.builder()
            .id(1)
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .profileImageUrl(null)
            .build();

        UpdateDto updateDto = new UpdateDto(); // All fields null

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO result = userService.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(user);
    }

    @Test
    void getUserPermissions_SuperAdminRole() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setType(UserType.SUPERADMIN);
        user.setActive(true);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissions = userService.getUserPermissions(1);

        // Assert
        assertNotNull(permissions);
        assertEquals(1, permissions.getUserId());
        assertEquals(UserType.SUPERADMIN, permissions.getRole());
        assertTrue(permissions.getCanCreatePost());
        assertTrue(permissions.getCanDeleteReplies());
        assertTrue(permissions.getCanBanUsers());
        assertTrue(permissions.getCanModifyPosts());
    }
    
    @Test
    void getUserPermissions_AdminRole() {
        // Arrange
        User user = new User();
        user.setId(2);
        user.setType(UserType.ADMIN);
        user.setActive(true);
        when(userRepo.findById(2)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissions = userService.getUserPermissions(2);

        // Assert
        assertNotNull(permissions);
        assertEquals(2, permissions.getUserId());
        assertEquals(UserType.ADMIN, permissions.getRole());
        assertTrue(permissions.getCanCreatePost());
        assertTrue(permissions.getCanDeleteReplies());
        assertTrue(permissions.getCanBanUsers());
        assertFalse(permissions.getCanModifyPosts());
    }
    @Test
    void getUserPermissions_NormalRole() {
        // Arrange
        User user = new User();
        user.setId(3);
        user.setType(UserType.NORMAL);
        user.setActive(true);
        when(userRepo.findById(3)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissions = userService.getUserPermissions(3);

        // Assert
        assertNotNull(permissions);
        assertEquals(3, permissions.getUserId());
        assertEquals(UserType.NORMAL, permissions.getRole());
        assertTrue(permissions.getCanCreatePost());
        assertFalse(permissions.getCanDeleteReplies());
        assertFalse(permissions.getCanBanUsers());
        assertTrue(permissions.getCanModifyPosts());
    }
    @Test
    void getUserPermissions_VisitorRole() {
        // Arrange
        User user = new User();
        user.setId(4);
        user.setType(UserType.VISITOR);
        user.setActive(true);
        when(userRepo.findById(4)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissions = userService.getUserPermissions(4);

        // Assert
        assertNotNull(permissions);
        assertEquals(4, permissions.getUserId());
        assertEquals(UserType.VISITOR, permissions.getRole());
        assertFalse(permissions.getCanCreatePost());
        assertFalse(permissions.getCanDeleteReplies());
        assertFalse(permissions.getCanBanUsers());
        assertFalse(permissions.getCanModifyPosts());
    }
    @Test
    void getUserPermissions_NullUserId() {
        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserPermissions(null));
        assertEquals("User not found with ID: null", exception.getMessage());
    }
    @Test
    void getUserPermissions_UserNotFound() {
        // Arrange
        when(userRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () ->
            userService.getUserPermissions(999));
        assertEquals("User not found with ID: 999", exception.getMessage());
        verify(userRepo, times(1)).findById(999);
    }


    @Test
    void getUserPermissions_UserFound() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setType(UserType.ADMIN);
        user.setActive(true);
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserPermissionsDTO permissions = userService.getUserPermissions(1);

        // Assert
        assertNotNull(permissions);
        assertEquals(1, permissions.getUserId());
        assertEquals(UserType.ADMIN, permissions.getRole());
        assertTrue(permissions.getCanCreatePost());
        assertTrue(permissions.getCanDeleteReplies());
        assertTrue(permissions.getCanBanUsers());
        assertFalse(permissions.getCanModifyPosts());
        verify(userRepo, times(1)).findById(1);
    }

    



}
