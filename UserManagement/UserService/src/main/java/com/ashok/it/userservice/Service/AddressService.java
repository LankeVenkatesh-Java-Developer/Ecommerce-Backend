package com.ashok.it.userservice.Service;


import com.ashok.it.userservice.Dto.AddressRequest;
import com.ashok.it.userservice.Dto.AddressResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AddressService {

    Mono<AddressResponse> addAddress(
            Long userId,
            AddressRequest request
    );

    Mono<AddressResponse> updateAddress(
            Long userId,
            Long addressId,
            AddressRequest request
    );

    Mono<Void> deleteAddress(
            Long userId,
            Long addressId
    );

    Mono<AddressResponse> getAddress(
            Long userId,
            Long addressId
    );

    Flux<AddressResponse> getAllAddresses(
            Long userId
    );
}
