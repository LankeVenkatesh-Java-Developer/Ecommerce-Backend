package com.ashok.it.userservice.Repository;

import com.ashok.it.userservice.Entity.PasswordResetToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PasswordResetTokenRepository extends R2dbcRepository<PasswordResetToken, Long> {
    Mono<PasswordResetToken> findByTokenHash(String tokenHash);

    Mono<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

    Mono<Void> deleteByUserId(Long userId);
}
