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
import com.venkatesh.it.usermanagementservice.repository.OtpRepository;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import com.venkatesh.it.usermanagementservice.security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_LENGTH = 6;

    public UserResponse register(RegisterRequest request) {
        return userService.registerUser(request);
    }

    public LoginResponse login(LoginRequest request) {
        String emailOrMobile = request.getEmailOrMobile();
        String password = request.getPassword();

        User user = userRepository.findByEmail(emailOrMobile)
                .orElseGet(() -> userRepository.findByMobileNumber(emailOrMobile)
                        .orElseThrow(() -> new BadRequestException("Invalid credentials")));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        if (user.getStatus() != com.venkatesh.it.usermanagementservice.model.enums.UserStatus.ACTIVE) {
            throw new BadRequestException("Account is not active. Status: " + user.getStatus());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(emailOrMobile, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public void forgetPassword(ForgetPasswordRequest request) throws MessagingException {
        String email = request.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account found with this email address"));

        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        otpRepository.deleteByEmail(email);

        Otp otpEntity = Otp.builder()
                .email(email)
                .otpCode(otp)
                .expiresAt(expiresAt)
                .isUsed(false)
                .build();

        otpRepository.save(otpEntity);

        emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(VerifyOtpRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();

        Otp otpEntity = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(email, otp)
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        return true;
    }

    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        Otp otpEntity = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse(email, otp)
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP has expired");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpEntity.setUsed(true);
        otpRepository.save(otpEntity);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
