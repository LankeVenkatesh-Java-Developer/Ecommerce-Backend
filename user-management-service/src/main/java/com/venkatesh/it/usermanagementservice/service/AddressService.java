package com.venkatesh.it.usermanagementservice.service;

import com.venkatesh.it.usermanagementservice.exception.BadRequestException;
import com.venkatesh.it.usermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.usermanagementservice.model.Address;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.dto.AddressRequest;
import com.venkatesh.it.usermanagementservice.model.dto.AddressResponse;
import com.venkatesh.it.usermanagementservice.repository.AddressRepository;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = Address.builder()
                .user(user)
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .addressType(request.getAddressType())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.unsetAllDefaultAddressesForUser(userId);
        }

        Address savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    public AddressResponse getAddressById(Long id, Long userId) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id + " for user: " + userId));
        return mapToAddressResponse(address);
    }

    public List<AddressResponse> getAllAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return addressRepository.findByUserId(userId).stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse getDefaultAddress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(this::mapToAddressResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No default address found for user: " + userId));
    }

    public List<AddressResponse> getAddressesByType(Long userId, String addressType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return addressRepository.findByUserIdAndAddressType(userId, 
                com.venkatesh.it.usermanagementservice.model.enums.AddressType.valueOf(addressType.toUpperCase()))
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse updateAddress(Long id, Long userId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id + " for user: " + userId));

        if (request.getAddressLine1() != null) {
            address.setAddressLine1(request.getAddressLine1());
        }

        if (request.getAddressLine2() != null) {
            address.setAddressLine2(request.getAddressLine2());
        }

        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }

        if (request.getState() != null) {
            address.setState(request.getState());
        }

        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }

        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }

        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }

        if (request.getIsDefault() != null) {
            if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
                addressRepository.unsetDefaultAddressForUser(userId, id);
            }
            address.setIsDefault(request.getIsDefault());
        }

        Address updatedAddress = addressRepository.save(address);
        return mapToAddressResponse(updatedAddress);
    }

    public AddressResponse setDefaultAddress(Long id, Long userId) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id + " for user: " + userId));

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            throw new BadRequestException("Address is already set as default");
        }

        addressRepository.unsetDefaultAddressForUser(userId, id);
        address.setIsDefault(true);

        Address updatedAddress = addressRepository.save(address);
        return mapToAddressResponse(updatedAddress);
    }

    public void deleteAddress(Long id, Long userId) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id + " for user: " + userId));

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            throw new BadRequestException("Cannot delete default address. Please set another address as default first.");
        }

        addressRepository.delete(address);
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .userId(address.getUser() != null ? address.getUser().getId() : null)
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .addressType(address.getAddressType())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
