package com.venkatesh.it.usermanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.usermanagementservice.config.TestSecurityConfig;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UpdateUserRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import com.venkatesh.it.usermanagementservice.security.UserPrincipal;
import com.venkatesh.it.usermanagementservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse testUserResponse;
    private RegisterRequest registerRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUserResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
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
    void getCurrentUser_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(testUserResponse);

        mockMvc.perform(get("/users/email/john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getUserByMobileNumber_Success() throws Exception {
        when(userService.getUserByMobileNumber("9876543210")).thenReturn(testUserResponse);

        mockMvc.perform(get("/users/mobile/9876543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value("9876543210"));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getUsersByStatus_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getUsersByStatus(UserStatus.ACTIVE)).thenReturn(users);

        mockMvc.perform(get("/users/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    void getUsersByRole_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getUsersByRole(UserRole.CUSTOMER)).thenReturn(users);

        mockMvc.perform(get("/users/role/CUSTOMER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("CUSTOMER"));
    }

    @Test
    void searchUsers_Success() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.searchUsers("John")).thenReturn(users);

        mockMvc.perform(get("/users/search?keyword=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .mobileNumber("9876543211")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .build();

        when(userService.updateUser(anyLong(), any(UpdateUserRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .status(UserStatus.INACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        when(userService.updateUserStatus(1L, UserStatus.INACTIVE)).thenReturn(updatedResponse);

        mockMvc.perform(patch("/users/1/status")
                        .with(csrf())
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void updateUserRole_Success() throws Exception {
        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .status(UserStatus.ACTIVE)
                .role(UserRole.ADMIN)
                .build();

        when(userService.updateUserRole(1L, UserRole.ADMIN)).thenReturn(updatedResponse);

        mockMvc.perform(patch("/users/1/role")
                        .with(csrf())
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserRoles_Success() throws Exception {
        when(userService.getUserRoles(1L)).thenReturn(new String[]{"CUSTOMER"});

        mockMvc.perform(get("/users/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("CUSTOMER"));
    }
}
