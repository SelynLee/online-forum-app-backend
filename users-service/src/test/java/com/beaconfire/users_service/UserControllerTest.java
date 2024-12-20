package com.beaconfire.users_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.beaconfire.users_service.controller.UserController;
import com.beaconfire.users_service.domain.User;
import com.beaconfire.users_service.domain.User.UserType;
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
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userService.findUserById(1)).thenReturn(userDTO);

        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User fetched successfully", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());
        verify(userService, times(1)).findUserById(1);
    }
    @Test
    public void testGetAllUsers_Success() {
        // Mock data
        List<UserDTO> mockUsers = List.of(
            UserDTO.builder()
                    .id(1)
                    .firstName("John")
                    .lastName("Doe")
                    .email("john@example.com")
                    .type(User.UserType.ADMIN) // Replace with the appropriate UserType enum value
                    .active(true)
                    .createdAt(LocalDateTime.now().minusDays(1)) // Example timestamp
                    .updatedAt(LocalDateTime.now())
                    .profileImageUrl("http://example.com/images/john.jpg")
                    .build(),
            UserDTO.builder()
                    .id(2)
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane@example.com")
                    .type(User.UserType.VISITOR) // Replace with the appropriate UserType enum value
                    .active(true)
                    .createdAt(LocalDateTime.now().minusDays(2)) // Example timestamp
                    .updatedAt(LocalDateTime.now())
                    .profileImageUrl("http://example.com/images/jane.jpg")
                    .build()
        );
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getAllUsers();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("Users fetched successfully.", response.getBody().getMessage());
        assertEquals(mockUsers, response.getBody().getData());
    }




    @Test
    void getUserById_UserNotFound() {
        // Arrange
        when(userService.findUserById(1)).thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(userService, times(1)).findUserById(1);
    }
    @Test
    public void testGetAllUsers_InternalServerError() {
        // Mock the service to throw an exception
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getAllUsers();

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    public void testGetAllUsers_NoUsers() {
        // Mock service to return an empty list
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getAllUsers();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("Users fetched successfully.", response.getBody().getMessage());
        assertTrue(((List<UserDTO>) response.getBody().getData()).isEmpty());
    }


    
    @Test
    void getUserById_InvalidUserId() {
        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(-1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Invalid user ID. ID must be greater than 0.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void getUserById_InternalServerError() {
        // Arrange
        when(userService.findUserById(1)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(1);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(userService, times(1)).findUserById(1);
    }




    @Test
    void updateUserProfile_Success() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("UpdatedFirstName");
        updateDto.setLastName("UpdatedLastName");

        UserDTO updatedUser = new UserDTO();
        updatedUser.setId(1);
        updatedUser.setFirstName("UpdatedFirstName");
        updatedUser.setLastName("UpdatedLastName");

        when(userService.updateUserProfile(1, updateDto)).thenReturn(updatedUser);

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User profile updated successfully.", response.getBody().getMessage());
        assertEquals(updatedUser, response.getBody().getData());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
    }


    @Test
    void updateUserProfile_UserNotFound() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("UpdatedFirstName");

        when(userService.updateUserProfile(1, updateDto))
                .thenThrow(new ResourceNotFoundException("User not found with ID: 1"));

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("User not found with ID: 1", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
    }


    @Test
    void updateUserProfile_UnexpectedError() {
        // Arrange
        UpdateDto updateDto = new UpdateDto();
        updateDto.setFirstName("UpdatedFirstName");

        when(userService.updateUserProfile(1, updateDto))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
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
//        assertTrue(response.getBody().isSuccess());
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
//        assertFalse(response.getBody().isSuccess());
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
//        assertFalse(response.getBody().isSuccess());
        assertEquals("Forbidden: Only Admin users can perform this action.", response.getBody().getMessage());
        verify(userService, times(1)).updateUserStatus(1, userDTO, 2);
    }
    @Test
    public void testGetUserPermissions_SuperAdmin() {
        // Mock data
        Integer userId = 1;
        UserPermissionsDTO mockPermissions = UserPermissionsDTO.builder()
                .userId(userId)
                .role(User.UserType.SUPERADMIN)
                .active(true)
                .canCreatePost(true)
                .canDeleteReplies(true)
                .canBanUsers(true)
                .canModifyPosts(true)
                .build();

        when(userService.getUserPermissions(userId)).thenReturn(mockPermissions);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserPermissions(userId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User permissions fetched successfully", response.getBody().getMessage());
        assertEquals(mockPermissions, response.getBody().getData());
    }
    @Test
    public void testGetUserPermissions_NormalUser() {
        // Mock data
        Integer userId = 2;
        UserPermissionsDTO mockPermissions = UserPermissionsDTO.builder()
                .userId(userId)
                .role(User.UserType.NORMAL)
                .active(true)
                .canCreatePost(true)
                .canDeleteReplies(false)
                .canBanUsers(false)
                .canModifyPosts(true)
                .build();

        when(userService.getUserPermissions(userId)).thenReturn(mockPermissions);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserPermissions(userId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User permissions fetched successfully", response.getBody().getMessage());
        assertEquals(mockPermissions, response.getBody().getData());
    }
    @Test
    public void testGetUserPermissions_UserNotFound() {
        // Mock data
        Integer userId = 999;

        when(userService.getUserPermissions(userId))
                .thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserPermissions(userId);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("User not found with ID: " + userId, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    public void testGetUserPermissions_UnexpectedError() {
        // Mock data
        Integer userId = 1;

        when(userService.getUserPermissions(userId))
                .thenThrow(new IllegalStateException("Unexpected error"));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserPermissions(userId);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void getUserById_NullUserId() {
        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Invalid user ID. ID must be greater than 0.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    void getUserById_UserIdLessThanOrEqualToZero() {
        // Act
        ResponseEntity<DataResponse> response = userController.getUserById(0);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Invalid user ID. ID must be greater than 0.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

  



    
    @Test
    public void testGetUserByEmail_Success() {
        // Mock data
        String email = "john@example.com";
        UserDTO mockUser = UserDTO.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .type(User.UserType.ADMIN) // Replace with appropriate type
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now())
                .profileImageUrl("http://example.com/images/john.jpg")
                .build();
        when(userService.findUserByEmail(email)).thenReturn(mockUser);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserByEmail(email);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User fetched successfully.", response.getBody().getMessage());
        assertEquals(mockUser, response.getBody().getData());
    }

    @Test
    public void testGetUserByEmail_UserNotFound() {
        // Mock data
        String email = "nonexistent@example.com";
        when(userService.findUserByEmail(email)).thenThrow(new ResourceNotFoundException("User not found"));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserByEmail(email);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void testGetUserByEmail_InvalidEmail() {
        // Mock data
        String email = "invalid-email";
        when(userService.findUserByEmail(email)).thenThrow(new IllegalArgumentException("Invalid email format."));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserByEmail(email);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Invalid email format.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
    @Test
    public void testGetUserByEmail_InternalServerError() {
        // Mock data
        String email = "error@example.com";
        when(userService.findUserByEmail(email)).thenThrow(new RuntimeException("Database error"));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.getUserByEmail(email);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("An unexpected error occurred.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    @Test
    void updateUserProfile_NoFieldsToUpdate() {
        // Arrange
        UpdateDto updateDto = new UpdateDto(); // All fields null
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        when(userService.updateUserProfile(1, updateDto)).thenReturn(userDTO);

        // Act
        ResponseEntity<DataResponse> response = userController.updateUserProfile(1, updateDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User profile updated successfully.", response.getBody().getMessage());
        assertEquals(userDTO, response.getBody().getData());
        verify(userService, times(1)).updateUserProfile(1, updateDto);
    }
    @Test
    public void testUpdateUserStatus_AdminSuccess() {
        // Mock data
        Integer userId = 2;
        Integer currentUserId = 1; // Admin user
        UserDTO inputUserDTO = UserDTO.builder()
                .active(false) // Ban the user
                .build();

        UserDTO updatedUserDTO = UserDTO.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .type(UserType.NORMAL)
                .active(false) // Updated status
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId)).thenReturn(updatedUserDTO);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User status updated successfully.", response.getBody().getMessage());
        assertEquals(updatedUserDTO, response.getBody().getData());
    }
    @Test
    public void testUpdateUserStatus_NonAdminForbidden() {
        // Mock data
        Integer userId = 2;
        Integer currentUserId = 3; // Non-admin user
        UserDTO inputUserDTO = UserDTO.builder()
                .active(false) // Attempt to ban the user
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId))
                .thenThrow(new RuntimeException("Forbidden: Only Admin users can perform this action."));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Forbidden: Only Admin users can perform this action.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    @Test
    public void testUpdateUserStatus_Success() {
        // Mock data
        Integer userId = 1;
        Integer currentUserId = 99; // Admin user
        UserDTO inputUserDTO = UserDTO.builder()
                .id(userId)
                .type(User.UserType.VISITOR) // Change user type
                .active(false) // Ban user
                .build();

        UserDTO updatedUserDTO = UserDTO.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .type(User.UserType.NORMAL)
                .active(false)
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId)).thenReturn(updatedUserDTO);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User status updated successfully.", response.getBody().getMessage());
        assertEquals(updatedUserDTO, response.getBody().getData());
    }
    @Test
    public void testUpdateUserStatus_UserNotFound() {
        // Mock data
        Integer userId = 2;
        Integer currentUserId = 1; // Admin user
        UserDTO inputUserDTO = UserDTO.builder()
                .active(false) // Attempt to ban the user
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId))
                .thenThrow(new ResourceNotFoundException("User not found with ID: " + userId));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("User not found with ID: " + userId, response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void testUpdateUserStatus_UpdateUserType() {
        // Mock data
        Integer userId = 2;
        Integer currentUserId = 1; // Admin user
        UserDTO inputUserDTO = UserDTO.builder()
                .type(UserType.SUPERADMIN) // Promote user to SUPERADMIN
                .build();

        UserDTO updatedUserDTO = UserDTO.builder()
                .id(userId)
                .firstName("Jane")
                .lastName("Doe")
                .type(UserType.SUPERADMIN) // Updated type
                .active(true)
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId)).thenReturn(updatedUserDTO);

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getSuccess());
        assertEquals("User status updated successfully.", response.getBody().getMessage());
        assertEquals(updatedUserDTO, response.getBody().getData());
    }
    @Test
    public void testUpdateUserStatus_CurrentUserNotFound() {
        // Mock data
        Integer userId = 2;
        Integer currentUserId = 99; // Non-existent current user
        UserDTO inputUserDTO = UserDTO.builder()
                .active(false) // Attempt to ban the user
                .build();

        when(userService.updateUserStatus(userId, inputUserDTO, currentUserId))
                .thenThrow(new ResourceNotFoundException("Current user not found."));

        // Perform the test
        ResponseEntity<DataResponse> response = userController.updateUserStatus(userId, inputUserDTO, currentUserId);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Current user not found.", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }




    
}

