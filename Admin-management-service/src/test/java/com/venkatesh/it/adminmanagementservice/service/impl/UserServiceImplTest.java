package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void whenGetUserRolesWithServiceDisabled_thenReturnDefaultAdminRole() {
        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void whenGetUserRolesWithServiceEnabled_thenReturnRoles() {
        // Note: This test assumes user.service.enabled is set to true via reflection
        // In actual implementation, this would be controlled by configuration
        when(restTemplate.getForObject(any(String.class), eq(String[].class)))
                .thenReturn(new String[]{"ADMIN", "USER"});

        // Since user.service.enabled defaults to false, we need to set it
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);

        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    void whenGetUserRolesWithEmptyResponse_thenReturnDefaultAdminRole() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(String[].class)))
                .thenReturn(new String[]{});

        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void whenGetUserRolesWithNullResponse_thenReturnDefaultAdminRole() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(String[].class)))
                .thenReturn(null);

        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void whenGetUserRolesWithServiceFailure_thenReturnDefaultAdminRole() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(String[].class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Set<String> roles = userService.getUserRoles(1L);

        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void whenIsValidUserWithServiceDisabled_thenReturnTrue() {
        boolean isValid = userService.isValidUser(1L);

        assertTrue(isValid);
    }

    @Test
    void whenIsValidUserWithServiceEnabled_thenReturnTrue() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(Boolean.class)))
                .thenReturn(true);

        boolean isValid = userService.isValidUser(1L);

        assertTrue(isValid);
    }

    @Test
    void whenIsValidUserWithServiceEnabledReturnsFalse_thenReturnFalse() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(Boolean.class)))
                .thenReturn(false);

        boolean isValid = userService.isValidUser(1L);

        assertFalse(isValid);
    }

    @Test
    void whenIsValidUserWithServiceFailure_thenReturnTrue() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(Boolean.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        boolean isValid = userService.isValidUser(1L);

        assertTrue(isValid);
    }

    @Test
    void whenIsValidUserWithNullResponse_thenReturnFalse() {
        org.springframework.test.util.ReflectionTestUtils.setField(userService, "userServiceEnabled", true);
        when(restTemplate.getForObject(any(String.class), eq(Boolean.class)))
                .thenReturn(null);

        boolean isValid = userService.isValidUser(1L);

        assertFalse(isValid);
    }
}
