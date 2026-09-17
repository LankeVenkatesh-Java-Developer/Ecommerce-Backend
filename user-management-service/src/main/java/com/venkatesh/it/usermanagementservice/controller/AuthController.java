package com.venkatesh.it.usermanagementservice.controller;

import com.venkatesh.it.usermanagementservice.model.dto.ForgetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginRequest;
import com.venkatesh.it.usermanagementservice.model.dto.LoginResponse;
import com.venkatesh.it.usermanagementservice.model.dto.RegisterRequest;
import com.venkatesh.it.usermanagementservice.model.dto.ResetPasswordRequest;
import com.venkatesh.it.usermanagementservice.model.dto.UserResponse;
import com.venkatesh.it.usermanagementservice.model.dto.VerifyOtpRequest;
import com.venkatesh.it.usermanagementservice.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/forget-password", "/forgot-password"})
    public ResponseEntity<String> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) throws MessagingException {
        authService.forgetPassword(request);
        return ResponseEntity.ok("OTP has been sent to your email address");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean isValid = authService.verifyOtp(request);
        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully");
    }
}
