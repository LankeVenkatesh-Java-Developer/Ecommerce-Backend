package com.venkatesh.it.usermanagementservice.controller;

import com.venkatesh.it.usermanagementservice.model.dto.AddressRequest;
import com.venkatesh.it.usermanagementservice.model.dto.AddressResponse;
import com.venkatesh.it.usermanagementservice.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Long userId, @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> getAddressById(@PathVariable Long id, @PathVariable Long userId) {
        AddressResponse response = addressService.getAddressById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAllAddressesByUserId(@PathVariable Long userId) {
        List<AddressResponse> responses = addressService.getAllAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/users/{userId}/default")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> getDefaultAddress(@PathVariable Long userId) {
        AddressResponse response = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/type/{addressType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<AddressResponse>> getAddressesByType(@PathVariable Long userId, @PathVariable String addressType) {
        List<AddressResponse> responses = addressService.getAddressesByType(userId, addressType);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> updateAddress(@PathVariable Long id, @PathVariable Long userId, @Valid @RequestBody AddressRequest request) {
        AddressResponse response = addressService.updateAddress(id, userId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/users/{userId}/default")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AddressResponse> setDefaultAddress(@PathVariable Long id, @PathVariable Long userId) {
        AddressResponse response = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id, @PathVariable Long userId) {
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }
}
