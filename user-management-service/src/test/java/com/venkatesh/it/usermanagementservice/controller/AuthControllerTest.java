package com.venkatesh.it.usermanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkatesh.it.usermanagementservice.config.TestSecurityConfig;
import com.venkatesh.it.usermanagementservice.model.dto.ForgetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginResponse;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.ResetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.model.dto.VerifyOtpRequest;
import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import com.venkatesh.it.usermanagementservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ForgetPasswordRequest forgetPasswordRequest;
    private VerifyOtpRequest verifyOtpRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private UserResponse userResponse;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("Password@123")
                .role(UserRole.CUSTOMER)
                .build();

        loginRequest = LoginRequest.builder()
                .emailOrMobile("john.doe@example.com")
                .password("Password@123")
                .build();

        forgetPasswordRequest = ForgetPasswordRequest.builder()
                .email("john.doe@example.com")
                .build();

        verifyOtpRequest = VerifyOtpRequest.builder()
                .email("john.doe@example.com")
                .otp("123456")
                .build();

        resetPasswordRequest = ResetPasswordRequest.builder()
                .email("john.doe@example.com")
                .otp("123456")
                .newPassword("NewPassword@123")
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .build();

        loginResponse = LoginResponse.builder()
                .token("jwt-token")
                .userId(1L)
                .email("john.doe@example.com")
                .role(UserRole.CUSTOMER.name())
                .build();
    }

    @Test
    void register_Success() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void login_Success() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void forgetPassword_Success() throws Exception {
        mockMvc.perform(post("/auth/forget-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email address"));
    }

    @Test
    void forgetPassword_WithAlternatePath_Success() throws Exception {
        mockMvc.perform(post("/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email address"));
    }

    @Test
    void verifyOtp_Success() throws Exception {
        when(authService.verifyOtp(any(VerifyOtpRequest.class))).thenReturn(true);

        mockMvc.perform(post("/auth/verify-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyOtpRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void resetPassword_Success() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully"));
    }
}
