package com.ashok.it.userservice.Repository;

import com.ashok.it.userservice.Entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByMobileNumber(String mobileNumber);
}
