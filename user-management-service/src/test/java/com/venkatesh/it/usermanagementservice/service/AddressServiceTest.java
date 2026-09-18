package com.venkatesh.it.usermanagementservice.service;

import com.venkatesh.it.usermanagementservice.exception.BadRequestException;
import com.venkatesh.it.usermanagementservice.exception.ResourceNotFoundException;
import com.venkatesh.it.usermanagementservice.model.Address;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.dto.AddressRequest;
import com.venkatesh.it.usermanagementservice.model.dto.AddressResponse;
import com.venkatesh.it.usermanagementservice.model.enums.AddressType;
import com.venkatesh.it.usermanagementservice.repository.AddressRepository;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private User testUser;
    private Address testAddress;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("encodedPassword")
                .build();

        testAddress = Address.builder()
                .id(1L)
                .user(testUser)
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .addressType(AddressType.HOME)
                .isDefault(false)
                .build();

        addressRequest = AddressRequest.builder()
                .addressLine1("123 Main St")
                .addressLine2("Apt 4B")
                .city("New York")
                .state("NY")
                .country("USA")
                .postalCode("10001")
                .addressType(AddressType.HOME)
                .isDefault(false)
                .build();
    }

    @Test
    void createAddress_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponse response = addressService.createAddress(1L, addressRequest);

        assertNotNull(response);
        assertEquals("123 Main St", response.getAddressLine1());
        assertEquals("New York", response.getCity());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void createAddress_WithDefault_Success() {
        addressRequest.setIsDefault(true);
        testAddress.setIsDefault(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponse response = addressService.createAddress(1L, addressRequest);

        assertNotNull(response);
        verify(addressRepository, times(1)).unsetAllDefaultAddressesForUser(1L);
    }

    @Test
    void createAddress_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.createAddress(1L, addressRequest));
    }

    @Test
    void getAddressById_Success() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));

        AddressResponse response = addressService.getAddressById(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAddressById_NotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddressById(1L, 1L));
    }

    @Test
    void getAllAddressesByUserId_Success() {
        Address address2 = Address.builder()
                .id(2L)
                .user(testUser)
                .addressLine1("456 Oak Ave")
                .city("Los Angeles")
                .state("CA")
                .country("USA")
                .postalCode("90001")
                .addressType(AddressType.WORK)
                .isDefault(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserId(1L)).thenReturn(Arrays.asList(testAddress, address2));

        List<AddressResponse> responses = addressService.getAllAddressesByUserId(1L);

        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void getAllAddressesByUserId_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAllAddressesByUserId(1L));
    }

    @Test
    void getDefaultAddress_Success() {
        testAddress.setIsDefault(true);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(testAddress));

        AddressResponse response = addressService.getDefaultAddress(1L);

        assertNotNull(response);
        assertTrue(response.getIsDefault());
    }

    @Test
    void getDefaultAddress_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getDefaultAddress(1L));
    }

    @Test
    void getDefaultAddress_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getDefaultAddress(1L));
    }

    @Test
    void getAddressesByType_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findByUserIdAndAddressType(1L, AddressType.HOME))
                .thenReturn(Arrays.asList(testAddress));

        List<AddressResponse> responses = addressService.getAddressesByType(1L, "HOME");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(AddressType.HOME, responses.get(0).getAddressType());
    }

    @Test
    void getAddressesByType_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAddressesByType(1L, "HOME"));
    }

    @Test
    void updateAddress_Success() {
        AddressRequest updateRequest = AddressRequest.builder()
                .addressLine1("789 Pine St")
                .city("Chicago")
                .state("IL")
                .country("USA")
                .postalCode("60601")
                .addressType(AddressType.WORK)
                .build();

        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponse response = addressService.updateAddress(1L, 1L, updateRequest);

        assertNotNull(response);
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void updateAddress_NotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.updateAddress(1L, 1L, addressRequest));
    }

    @Test
    void updateAddress_SetDefault_Success() {
        addressRequest.setIsDefault(true);

        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponse response = addressService.updateAddress(1L, 1L, addressRequest);

        assertNotNull(response);
        verify(addressRepository, times(1)).unsetDefaultAddressForUser(1L, 1L);
    }

    @Test
    void setDefaultAddress_Success() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));
        when(addressRepository.save(any(Address.class))).thenReturn(testAddress);

        AddressResponse response = addressService.setDefaultAddress(1L, 1L);

        assertNotNull(response);
        verify(addressRepository, times(1)).unsetDefaultAddressForUser(1L, 1L);
        assertTrue(testAddress.getIsDefault());
    }

    @Test
    void setDefaultAddress_NotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.setDefaultAddress(1L, 1L));
    }

    @Test
    void setDefaultAddress_AlreadyDefault() {
        testAddress.setIsDefault(true);
        
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));

        assertThrows(BadRequestException.class, () -> addressService.setDefaultAddress(1L, 1L));
    }

    @Test
    void deleteAddress_Success() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressRepository).delete(any(Address.class));

        addressService.deleteAddress(1L, 1L);

        verify(addressRepository, times(1)).delete(any(Address.class));
    }

    @Test
    void deleteAddress_NotFound() {
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.deleteAddress(1L, 1L));
    }

    @Test
    void deleteAddress_DefaultAddress() {
        testAddress.setIsDefault(true);
        
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));

        assertThrows(BadRequestException.class, () -> addressService.deleteAddress(1L, 1L));
    }
}
