package com.beaconfire.users_service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void findUserById_UserExists_ReturnsUserDTO() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setType(UserType.NORMAL);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // Act
        UserDTO userDTO = userService.findUserById(1);

        // Assert
        assertNotNull(userDTO);
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
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
    void updateUserProfile_UserExists_UpdatesUser() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("jane.smith@example.com");

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        // Act
        UserDTO updatedUser = userService.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals("jane.smith@example.com", updatedUser.getEmail());
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updateUserProfile_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("Jane");

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUserProfile(1, updateDto)
        );
        assertEquals("User not found with ID: 1", exception.getMessage());
        verify(userRepo, times(1)).findById(1);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void updateUserStatus_AdminUpdatesUser_ReturnsUpdatedUser() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(2);
        currentUser.setType(UserType.ADMIN);

        User userToUpdate = new User();
        userToUpdate.setId(1);
        userToUpdate.setActive(false);
        userToUpdate.setType(UserType.NORMAL);

        UserDTO userDTO = new UserDTO();
        userDTO.setActive(true);
        userDTO.setType(UserType.SUPERADMIN);

        when(userRepo.findById(2)).thenReturn(Optional.of(currentUser));
        when(userRepo.findById(1)).thenReturn(Optional.of(userToUpdate));
        when(userRepo.save(any(User.class))).thenReturn(userToUpdate);

        // Act
        UserDTO updatedUser = userService.updateUserStatus(1, userDTO, 2);

        // Assert
        assertNotNull(updatedUser);
        assertTrue(updatedUser.getActive());
        assertEquals(UserType.SUPERADMIN, updatedUser.getType());
        verify(userRepo, times(2)).findById(anyInt());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void updateUserStatus_NonAdminUser_ThrowsRuntimeException() {
        // Arrange
        User currentUser = new User();
        currentUser.setId(2);
        currentUser.setType(UserType.NORMAL);

        UserDTO userDTO = new UserDTO();
        userDTO.setActive(true);

        when(userRepo.findById(2)).thenReturn(Optional.of(currentUser));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.updateUserStatus(1, userDTO, 2)
        );
        assertEquals("Forbidden: Only Admin users can perform this action.", exception.getMessage());
        verify(userRepo, times(1)).findById(2);
        verify(userRepo, never()).findById(1);
        verify(userRepo, never()).save(any(User.class));
    }
}
