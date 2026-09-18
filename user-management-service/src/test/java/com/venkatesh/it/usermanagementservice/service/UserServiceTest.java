package com.venkatesh.it.usermanagementservice.service;

import com.venkatesh.it.usermanagementservice.exception.BadRequestException;
import com.venkatesh.it.usermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UpdateUserRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("Password@123")
                .role(UserRole.CUSTOMER)
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .mobileNumber("9876543211")
                .role(UserRole.ADMIN)
                .build();
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("john.doe@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_MobileNumberAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByMobileNumber(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_InvalidEmail() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .firstName("John")
                .email("invalid-email")
                .mobileNumber("9876543210")
                .password("Password@123")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registerUser(invalidRequest));
    }

    @Test
    void registerUser_InvalidMobileNumber() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .firstName("John")
                .email("john.doe@example.com")
                .mobileNumber("1234567890")
                .password("Password@123")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registerUser(invalidRequest));
    }

    @Test
    void registerUser_InvalidPassword() {
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .firstName("John")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("weak")
                .build();

        assertThrows(BadRequestException.class, () -> userService.registerUser(invalidRequest));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(response);
        assertEquals("john.doe@example.com", response.getEmail());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("nonexistent@example.com"));
    }

    @Test
    void getUserByMobileNumber_Success() {
        when(userRepository.findByMobileNumber("9876543210")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserByMobileNumber("9876543210");

        assertNotNull(response);
        assertEquals("9876543210", response.getMobileNumber());
    }

    @Test
    void getUserByMobileNumber_NotFound() {
        when(userRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByMobileNumber("1234567890"));
    }

    @Test
    void getAllUsers_Success() {
        User user2 = User.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .mobileNumber("9876543211")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<UserResponse> responses = userService.getAllUsers();

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void getUsersByStatus_Success() {
        when(userRepository.findByStatus(UserStatus.ACTIVE)).thenReturn(Arrays.asList(testUser));

        List<UserResponse> responses = userService.getUsersByStatus(UserStatus.ACTIVE);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(UserStatus.ACTIVE, responses.get(0).getStatus());
    }

    @Test
    void getUsersByRole_Success() {
        when(userRepository.findByRole(UserRole.CUSTOMER)).thenReturn(Arrays.asList(testUser));

        List<UserResponse> responses = userService.getUsersByRole(UserRole.CUSTOMER);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(UserRole.CUSTOMER, responses.get(0).getRole());
    }

    @Test
    void searchUsers_Success() {
        when(userRepository.searchByKeyword("John")).thenReturn(Arrays.asList(testUser));

        List<UserResponse> responses = userService.searchUsers("John");

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(userRepository.existsByMobileNumber("9876543211")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(1L, updateUserRequest);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateUserRequest));
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.updateUser(1L, updateUserRequest));
    }

    @Test
    void updateUserStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUserStatus(1L, UserStatus.INACTIVE);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserStatus_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserStatus(1L, UserStatus.INACTIVE));
    }

    @Test
    void updateUserRole_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUserRole(1L, UserRole.ADMIN);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserRole_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUserRole(1L, UserRole.ADMIN));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserRoles_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String[] roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertEquals(1, roles.length);
        assertEquals(UserRole.CUSTOMER.name(), roles[0]);
    }

    @Test
    void getUserRoles_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserRoles(1L));
    }
}
