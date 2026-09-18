package com.venkatesh.it.usermanagementservice.service;

import com.venkatesh.it.usermanagementservice.exception.BadRequestException;
import com.venkatesh.it.usermanagementservice.model.Otp;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.dto.ForgetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginResponse;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.ResetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.model.dto.VerifyOtpRequest;
import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import com.venkatesh.it.usermanagementservice.repository.OtpRepository;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import com.venkatesh.it.usermanagementservice.security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private ForgetPasswordRequest forgetPasswordRequest;
    private VerifyOtpRequest verifyOtpRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private Authentication authentication;

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

        authentication = mock(Authentication.class);
    }

    @Test
    void register_Success() {
        UserResponse expectedResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .email("john.doe@example.com")
                .build();

        when(userService.registerUser(registerRequest)).thenReturn(expectedResponse);

        UserResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        verify(userService, times(1)).registerUser(registerRequest);
    }

    @Test
    void login_WithEmail_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", "encodedPassword")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals(UserRole.CUSTOMER.name(), response.getRole());
    }

    @Test
    void login_WithMobileNumber_Success() {
        loginRequest.setEmailOrMobile("9876543210");
        
        when(userRepository.findByEmail("9876543210")).thenReturn(Optional.empty());
        when(userRepository.findByMobileNumber("9876543210")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", "encodedPassword")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void login_InvalidCredentials_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_InvalidCredentials_WrongPassword() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", "encodedPassword")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void login_AccountNotActive() {
        testUser.setStatus(UserStatus.INACTIVE);
        
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", "encodedPassword")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.login(loginRequest));
    }

    @Test
    void forgetPassword_Success() throws MessagingException {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        authService.forgetPassword(forgetPasswordRequest);

        verify(otpRepository, times(1)).deleteByEmail("john.doe@example.com");
        verify(otpRepository, times(1)).save(any(Otp.class));
        verify(emailService, times(1)).sendOtpEmail(eq("john.doe@example.com"), anyString());
    }

    @Test
    void forgetPassword_UserNotFound() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.forgetPassword(forgetPasswordRequest));
    }

    @Test
    void verifyOtp_Success() {
        Otp otp = Otp.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();

        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("john.doe@example.com", "123456"))
                .thenReturn(Optional.of(otp));

        boolean result = authService.verifyOtp(verifyOtpRequest);

        assertTrue(result);
    }

    @Test
    void verifyOtp_InvalidOtp() {
        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.verifyOtp(verifyOtpRequest));
    }

    @Test
    void verifyOtp_ExpiredOtp() {
        Otp otp = Otp.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .isUsed(false)
                .build();

        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("john.doe@example.com", "123456"))
                .thenReturn(Optional.of(otp));

        assertThrows(BadRequestException.class, () -> authService.verifyOtp(verifyOtpRequest));
    }

    @Test
    void resetPassword_Success() {
        Otp otp = Otp.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();

        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("john.doe@example.com", "123456"))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("NewPassword@123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(otpRepository.save(any(Otp.class))).thenReturn(otp);

        authService.resetPassword(resetPasswordRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(otpRepository, times(1)).save(any(Otp.class));
        assertTrue(otp.isUsed());
    }

    @Test
    void resetPassword_InvalidOtp() {
        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.resetPassword(resetPasswordRequest));
    }

    @Test
    void resetPassword_ExpiredOtp() {
        Otp otp = Otp.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(5))
                .isUsed(false)
                .build();

        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("john.doe@example.com", "123456"))
                .thenReturn(Optional.of(otp));

        assertThrows(BadRequestException.class, () -> authService.resetPassword(resetPasswordRequest));
    }

    @Test
    void resetPassword_UserNotFound() {
        Otp otp = Otp.builder()
                .id(1L)
                .email("john.doe@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();

        when(otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("john.doe@example.com", "123456"))
                .thenReturn(Optional.of(otp));
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> authService.resetPassword(resetPasswordRequest));
    }
}
