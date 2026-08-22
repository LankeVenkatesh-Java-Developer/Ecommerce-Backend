package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.AddressRequest;
import com.ashok.it.userservice.Dto.AddressResponse;
import com.ashok.it.userservice.Service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public Mono<AddressResponse> addAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequest request,
            ServerWebExchange exchange) {

        return addressService.addAddress(userId, request)
                .map(response -> {
                    exchange.getResponse().setStatusCode(HttpStatus.CREATED);
                    return response;
                });
    }

    @PutMapping("/{addressId}")
    public Mono<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {

        return addressService.updateAddress(
                userId,
                addressId,
                request
        );
    }

    @GetMapping("/{addressId}")
    public Mono<AddressResponse> getAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return addressService.getAddress(
                userId,
                addressId
        );
    }

    @GetMapping
    public Flux<AddressResponse> getAllAddresses(
            @PathVariable Long userId) {

        return addressService.getAllAddresses(userId);
    }

    @DeleteMapping("/{addressId}")
    public Mono<String> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        return addressService.deleteAddress(
                userId,
                addressId
        )
        .thenReturn("Address deleted successfully");
    }
}