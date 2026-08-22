package com.ashok.it.userservice.Service;

import com.ashok.it.userservice.Dto.LoginRequest;
import com.ashok.it.userservice.Dto.LoginResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<LoginResponse> login(LoginRequest request);
}