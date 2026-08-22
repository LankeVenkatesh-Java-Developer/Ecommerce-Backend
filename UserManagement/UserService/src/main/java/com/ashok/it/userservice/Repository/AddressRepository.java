package com.ashok.it.userservice.Repository;

import com.ashok.it.userservice.Entity.Address;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AddressRepository extends R2dbcRepository<Address, Long> {
    Flux<Address> findByUserIdAndDeletedFalse(Long userId);

    Mono<Address> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
}
