package com.venkatesh.it.usermanagementservice.controller;

import com.venkatesh.it.usermanagementservice.model.dto.AddressRequest;
import com.venkatesh.it.usermanagementservice.model.dto.AddressResponse;
import com.venkatesh.it.usermanagementservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressResponse> createAddress(@Valid @RequestBody AddressRequest request, Authentication authentication) {
        com.venkatesh.it.usermanagementservice.security.UserPrincipal userPrincipal = 
            (com.venkatesh.it.usermanagementservice.security.UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        AddressResponse response = addressService.createAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> createAddressForUser(@PathVariable Long userId, @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AddressResponse>> getAllAddresses(Authentication authentication) {
        com.venkatesh.it.usermanagementservice.security.UserPrincipal userPrincipal = 
            (com.venkatesh.it.usermanagementservice.security.UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        List<AddressResponse> responses = addressService.getAllAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id, @PathVariable Long userId) {
        AddressResponse response = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAllAddressesByUserId(@PathVariable Long userId) {
        List<AddressResponse> responses = addressService.getAllAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{userId}/default")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> getDefaultAddress(@PathVariable Long userId) {
        AddressResponse response = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/type/{addressType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesByType(@PathVariable Long userId, @PathVariable String addressType) {
        List<AddressResponse> responses = addressService.getAddressesByType(userId, addressType);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressRequest request, Authentication authentication) {
        com.venkatesh.it.usermanagementservice.security.UserPrincipal userPrincipal =
            (com.venkatesh.it.usermanagementservice.security.UserPrincipal) authentication.getPrincipal();
        Long userId = userPrincipal.getId();
        AddressResponse response = addressService.updateAddress(addressId, userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> updateAddressForUser(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {

        AddressResponse response = addressService.updateAddress(addressId, userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/{userId}/default")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id, @PathVariable Long userId) {
        AddressResponse response = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId, userId);

        return ResponseEntity.noContent().build();
    }
}
