package com.ashok.it.userservice.Service;

import com.ashok.it.userservice.Dto.RegisterRequest;
import com.ashok.it.userservice.Dto.UserResponse;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserResponse> registerUser(RegisterRequest request);

    Mono<UserResponse> getUserById(Long userId);
}
