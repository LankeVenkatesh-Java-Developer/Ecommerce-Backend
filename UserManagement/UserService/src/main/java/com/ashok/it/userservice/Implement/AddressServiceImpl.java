package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.AddressRequest;
import com.ashok.it.userservice.Dto.AddressResponse;
import com.ashok.it.userservice.Entity.Address;
import com.ashok.it.userservice.Exception.AddressNotFoundException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.AddressRepository;
import com.ashok.it.userservice.Repository.UserRepository;
import com.ashok.it.userservice.Service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Mono<AddressResponse> addAddress(
            Long userId,
            AddressRequest request) {

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "User not found with id: " + userId
                )))
                .flatMap(user -> {
                    Address address = new Address();
                    address.setUserId(userId);
                    address.setAddressLine1(request.getAddressLine1());
                    address.setAddressLine2(request.getAddressLine2());
                    address.setCity(request.getCity());
                    address.setState(request.getState());
                    address.setCountry(request.getCountry());
                    address.setPostalCode(request.getPostalCode());
                    address.setAddressType(request.getAddressType());

                    address.setIsDefault(
                            request.getIsDefault() != null
                                    ? request.getIsDefault()
                                    : false
                    );

                    address.setDeleted(false);
                    address.setCreatedAt(LocalDateTime.now());
                    address.setUpdatedAt(LocalDateTime.now());

                    return addressRepository.save(address)
                            .map(this::mapToResponse);
                });
    }

    @Override
    public Mono<AddressResponse> updateAddress(
            Long userId,
            Long addressId,
            AddressRequest request) {

        return addressRepository
                .findByIdAndUserIdAndDeletedFalse(
                        addressId,
                        userId
                )
                .switchIfEmpty(Mono.error(new AddressNotFoundException(
                        "Address not found"
                )))
                .flatMap(address -> {
                    address.setAddressLine1(request.getAddressLine1());
                    address.setAddressLine2(request.getAddressLine2());
                    address.setCity(request.getCity());
                    address.setState(request.getState());
                    address.setCountry(request.getCountry());
                    address.setPostalCode(request.getPostalCode());
                    address.setAddressType(request.getAddressType());

                    if (request.getIsDefault() != null) {
                        address.setIsDefault(request.getIsDefault());
                    }

                    address.setUpdatedAt(LocalDateTime.now());

                    return addressRepository.save(address)
                            .map(this::mapToResponse);
                });
    }

    @Override
    public Mono<Void> deleteAddress(
            Long userId,
            Long addressId) {

        return addressRepository
                .findByIdAndUserIdAndDeletedFalse(
                        addressId,
                        userId
                )
                .switchIfEmpty(Mono.error(new AddressNotFoundException(
                        "Address not found"
                )))
                .flatMap(address -> {
                    address.setDeleted(true);
                    address.setUpdatedAt(LocalDateTime.now());
                    return addressRepository.save(address);
                })
                .then();
    }

    @Override
    public Mono<AddressResponse> getAddress(
            Long userId,
            Long addressId) {

        return addressRepository
                .findByIdAndUserIdAndDeletedFalse(
                        addressId,
                        userId
                )
                .switchIfEmpty(Mono.error(new AddressNotFoundException(
                        "Address not found"
                )))
                .map(this::mapToResponse);
    }

    @Override
    public Flux<AddressResponse> getAllAddresses(
            Long userId) {

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(
                        "User not found with id: " + userId
                )))
                .flatMapMany(user -> addressRepository
                        .findByUserIdAndDeletedFalse(userId)
                        .map(this::mapToResponse));
    }


    private AddressResponse mapToResponse(
            Address address) {

        AddressResponse response =
                new AddressResponse();

        response.setId(address.getId());
        response.setUserId(address.getUserId());
        response.setAddressLine1(address.getAddressLine1());
        response.setAddressLine2(address.getAddressLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPostalCode(address.getPostalCode());
        response.setAddressType(address.getAddressType());
        response.setIsDefault(address.getIsDefault());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());

        return response;
    }
}
