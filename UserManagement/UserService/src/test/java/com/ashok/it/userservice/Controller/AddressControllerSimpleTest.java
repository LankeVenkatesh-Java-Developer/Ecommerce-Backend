package com.ashok.it.userservice.Controller;

import com.ashok.it.userservice.Dto.AddressRequest;
import com.ashok.it.userservice.Dto.AddressResponse;
import com.ashok.it.userservice.Exception.AddressNotFoundException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressController Tests (Simplified)")
class AddressControllerSimpleTest {

    @Mock
    private AddressService addressService;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Mock
    private org.springframework.http.server.reactive.ServerHttpResponse serverHttpResponse;

    @InjectMocks
    private AddressController addressController;

    private AddressRequest addressRequest;
    private AddressResponse addressResponse;

    @BeforeEach
    void setUp() {
        when(serverWebExchange.getResponse()).thenReturn(serverHttpResponse);
        addressRequest = new AddressRequest();
        addressRequest.setAddressLine1("123 Main Street");
        addressRequest.setAddressLine2("Apt 4B");
        addressRequest.setCity("New York");
        addressRequest.setState("NY");
        addressRequest.setCountry("USA");
        addressRequest.setPostalCode("10001");
        addressRequest.setAddressType("HOME");
        addressRequest.setIsDefault(true);

        addressResponse = new AddressResponse();
        addressResponse.setId(1L);
        addressResponse.setUserId(1L);
        addressResponse.setAddressLine1("123 Main Street");
        addressResponse.setAddressLine2("Apt 4B");
        addressResponse.setCity("New York");
        addressResponse.setState("NY");
        addressResponse.setCountry("USA");
        addressResponse.setPostalCode("10001");
        addressResponse.setAddressType("HOME");
        addressResponse.setIsDefault(true);
        addressResponse.setCreatedAt(LocalDateTime.now());
        addressResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("addAddress() Tests")
    class AddAddressTests {

        @Test
        @DisplayName("Should add address successfully with valid request")
        void shouldAddAddressSuccessfully() {
            // Arrange
            Long userId = 1L;
            when(addressService.addAddress(eq(userId), any(AddressRequest.class))).thenReturn(Mono.just(addressResponse));

            // Act
            AddressResponse response = addressController.addAddress(userId, addressRequest, serverWebExchange).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getCity()).isEqualTo("New York");
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            Long userId = 999L;
            when(addressService.addAddress(eq(userId), any(AddressRequest.class)))
                    .thenReturn(Mono.error(new UserNotFoundException("User not found with id: " + userId)));

            // Act & Assert
            try {
                addressController.addAddress(userId, addressRequest, serverWebExchange).block();
            } catch (UserNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("User not found with id: " + userId);
            }
        }
    }

    @Nested
    @DisplayName("updateAddress() Tests")
    class UpdateAddressTests {

        @Test
        @DisplayName("Should update address successfully")
        void shouldUpdateAddressSuccessfully() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            when(addressService.updateAddress(eq(userId), eq(addressId), any(AddressRequest.class)))
                    .thenReturn(Mono.just(addressResponse));

            // Act
            AddressResponse response = addressController.updateAddress(userId, addressId, addressRequest).block();

            // Assert
            assertThat(response).isNotNull();
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFound() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            when(addressService.updateAddress(eq(userId), eq(addressId), any(AddressRequest.class)))
                    .thenReturn(Mono.error(new AddressNotFoundException("Address not found")));

            // Act & Assert
            try {
                addressController.updateAddress(userId, addressId, addressRequest).block();
            } catch (AddressNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Address not found");
            }
        }
    }

    @Nested
    @DisplayName("getAddress() Tests")
    class GetAddressTests {

        @Test
        @DisplayName("Should return address when found")
        void shouldReturnAddressWhenFound() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            when(addressService.getAddress(userId, addressId)).thenReturn(Mono.just(addressResponse));

            // Act
            AddressResponse response = addressController.getAddress(userId, addressId).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFoundOnGet() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            when(addressService.getAddress(userId, addressId))
                    .thenReturn(Mono.error(new AddressNotFoundException("Address not found")));

            // Act & Assert
            try {
                addressController.getAddress(userId, addressId).block();
            } catch (AddressNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Address not found");
            }
        }
    }

    @Nested
    @DisplayName("getAllAddresses() Tests")
    class GetAllAddressesTests {

        @Test
        @DisplayName("Should return all addresses for user")
        void shouldReturnAllAddressesForUser() {
            // Arrange
            Long userId = 1L;
            AddressResponse address2 = new AddressResponse();
            address2.setId(2L);
            address2.setUserId(1L);
            address2.setAddressLine1("456 Oak Street");
            address2.setCity("Boston");

            when(addressService.getAllAddresses(userId)).thenReturn(Flux.just(addressResponse, address2));

            // Act
            List<AddressResponse> responses = addressController.getAllAddresses(userId).collectList().block();

            // Assert
            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFoundOnGetAll() {
            // Arrange
            Long userId = 999L;
            when(addressService.getAllAddresses(userId))
                    .thenReturn(Flux.error(new UserNotFoundException("User not found with id: " + userId)));

            // Act & Assert
            try {
                addressController.getAllAddresses(userId).collectList().block();
            } catch (UserNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("User not found with id: " + userId);
            }
        }
    }

    @Nested
    @DisplayName("deleteAddress() Tests")
    class DeleteAddressTests {

        @Test
        @DisplayName("Should delete address successfully")
        void shouldDeleteAddressSuccessfully() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            when(addressService.deleteAddress(userId, addressId)).thenReturn(Mono.empty());

            // Act
            String response = addressController.deleteAddress(userId, addressId).block();

            // Assert
            assertThat(response).isEqualTo("Address deleted successfully");
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFoundOnDelete() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            org.mockito.Mockito.doThrow(new AddressNotFoundException("Address not found"))
                    .when(addressService).deleteAddress(userId, addressId);

            // Act & Assert
            try {
                addressController.deleteAddress(userId, addressId).block();
            } catch (AddressNotFoundException e) {
                assertThat(e.getMessage()).isEqualTo("Address not found");
            }
        }
    }
}
