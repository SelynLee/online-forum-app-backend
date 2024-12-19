package com.beaconfire.users_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.beaconfire.users_service.controller.UserController;
import com.beaconfire.users_service.dto.DataResponse;
import com.beaconfire.users_service.dto.UpdateDto;
import com.beaconfire.users_service.dto.UserDTO;
import com.beaconfire.users_service.dto.UserPermissionsDTO;
import com.beaconfire.users_service.exception.ResourceNotFoundException;
import com.beaconfire.users_service.service.UserService;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_Success() {
        // Arrange
        UserDTO mockUser = new UserDTO();
        mockUser.setId(1);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(userService.findUserById(1)).thenReturn(mockUser);

        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(1);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User fetched successfully", response.getBody().getMessage());
        verify(userService, times(1)).findUserById(1);
    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        when(userService.findUserById(1)).thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(1);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        verify(userService, times(1)).findUserById(1);
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");

        when(userService.updateUserProfile(1, updateDto)).thenReturn(updatedUser);

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User profile updated successfully.", response.getBody().getMessage());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
    }

    @Test
    void updateUserProfile_UserNotFound() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("Jane");

        when(userService.updateUserProfile(1, updateDto)).thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
    }

    @Test
    void getUserPermissions_Success() {
        // Arrange
        UserPermissionsDTO permissions = new UserPermissionsDTO();
        permissions.setUserId(1);
        permissions.setCanCreatePost(true);

        when(userService.getUserPermissions(1)).thenReturn(permissions);

        // Act
        ResponseEntity<DataResponse> response = userController.getUserPermissions(1);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User permissions fetched successfully", response.getBody().getMessage());
        verify(userService, times(1)).getUserPermissions(1);
    }

    @Test
    void getUserPermissions_UserNotFound() {
        // Arrange
        when(userService.getUserPermissions(1)).thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.getUserPermissions(1);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        verify(userService, times(1)).getUserPermissions(1);
    }

    @Test
    void updateUserStatus_Success() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setActive(true);

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1);
        updatedUser.setActive(true);

        when(userService.updateUserStatus(1, userDTO, 2)).thenReturn(updatedUser);

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserStatus(1, userDTO, 2);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User status updated successfully.", response.getBody().getMessage());
        verify(userService, times(1)).updateUserStatus(1, userDTO, 2);
    }

    @Test
    void updateUserStatus_UserNotFound() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setActive(true);

        when(userService.updateUserStatus(1, userDTO, 2)).thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserStatus(1, userDTO, 2);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        verify(userService, times(1)).updateUserStatus(1, userDTO, 2);
    }

    @Test
    void updateUserStatus_Forbidden() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setActive(true);

        when(userService.updateUserStatus(1, userDTO, 2)).thenThrow(new RuntimeException("Forbidden: Only Admin users can perform this action."));

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserStatus(1, userDTO, 2);

        // Assert
        assertNotNull(response);
        assertEquals(403, response.getStatusCodeValue());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Forbidden: Only Admin users can perform this action.", response.getBody().getMessage());
        verify(userService, times(1)).updateUserStatus(1, userDTO, 2);
    }
}

