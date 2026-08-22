package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.RegisterRequest;
import com.ashok.it.userservice.Dto.UserResponse;
import com.ashok.it.userservice.Exception.DuplicateUserException;
import com.ashok.it.userservice.Repository.UserRepository;
import com.ashok.it.userservice.Service.UserService;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.InvalidPasswordException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Event.UserEventPublisher;
import com.ashok.it.userservice.Service.NotificationServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;
    private final NotificationServiceClient notificationServiceClient;

    @Override
    public Mono<UserResponse> registerUser(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Mono.error(new InvalidPasswordException(
                    "Password and confirm password do not match"
            ));
        }

        return userRepository.existsByEmail(request.getEmail())
                .flatMap(emailExists -> {
                    if (emailExists) {
                        return Mono.error(new DuplicateUserException(
                                "Email already registered"
                        ));
                    }
                    return userRepository.existsByMobileNumber(request.getMobileNumber());
                })
                .flatMap(mobileExists -> {
                    if (mobileExists) {
                        return Mono.error(new DuplicateUserException(
                                "Mobile number already registered"
                        ));
                    }
                    
                    User user = new User();
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    user.setEmail(request.getEmail());
                    user.setMobileNumber(request.getMobileNumber());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setStatus("ACTIVE");
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                UserResponse response = mapToResponse(savedUser);
                                return userEventPublisher.publishUserCreatedEvent(response)
                                        .then(notificationServiceClient.sendWelcomeEmail(user.getEmail(), user.getId()))
                                        .thenReturn(response);
                            });
                });
    }

    @Override
    public Mono<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "User not found with id: " + userId
                )))
                .map(this::mapToResponse);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setMobileNumber(user.getMobileNumber());
        response.setStatus(user.getStatus());
        return response;
    }
}