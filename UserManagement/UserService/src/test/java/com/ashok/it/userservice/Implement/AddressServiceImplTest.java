package com.ashok.it.userservice.Implement;

import com.ashok.it.userservice.Dto.AddressRequest;
import com.ashok.it.userservice.Dto.AddressResponse;
import com.ashok.it.userservice.Entity.Address;
import com.ashok.it.userservice.Entity.User;
import com.ashok.it.userservice.Exception.AddressNotFoundException;
import com.ashok.it.userservice.Exception.UserNotFoundException;
import com.ashok.it.userservice.Repository.AddressRepository;
import com.ashok.it.userservice.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddressServiceImpl Tests")
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private User user;
    private Address address;
    private AddressRequest addressRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setMobileNumber("9876543210");
        user.setPassword("encodedPassword");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        address = new Address();
        address.setId(1L);
        address.setUserId(1L);
        address.setAddressLine1("123 Main Street");
        address.setAddressLine2("Apt 4B");
        address.setCity("New York");
        address.setState("NY");
        address.setCountry("USA");
        address.setPostalCode("10001");
        address.setAddressType("HOME");
        address.setIsDefault(true);
        address.setDeleted(false);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());

        addressRequest = new AddressRequest();
        addressRequest.setAddressLine1("123 Main Street");
        addressRequest.setAddressLine2("Apt 4B");
        addressRequest.setCity("New York");
        addressRequest.setState("NY");
        addressRequest.setCountry("USA");
        addressRequest.setPostalCode("10001");
        addressRequest.setAddressType("HOME");
        addressRequest.setIsDefault(true);
    }

    @Nested
    @DisplayName("addAddress() Tests")
    class AddAddressTests {

        @Test
        @DisplayName("Should add address successfully for valid user")
        void shouldAddAddressSuccessfully() {
            // Arrange
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Mono.just(user));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            AddressResponse response = addressService.addAddress(userId, addressRequest).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUserId()).isEqualTo(1L);
            assertThat(response.getAddressLine1()).isEqualTo("123 Main Street");
            assertThat(response.getCity()).isEqualTo("New York");
            assertThat(response.getIsDefault()).isTrue();

            verify(userRepository).findById(userId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFound() {
            // Arrange
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> addressService.addAddress(userId, addressRequest).block())
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found with id: " + userId);

            verify(userRepository).findById(userId);
            verify(addressRepository, never()).save(any(Address.class));
        }

        @Test
        @DisplayName("Should set isDefault to false when not provided")
        void shouldSetDefaultToFalseWhenNotProvided() {
            // Arrange
            addressRequest.setIsDefault(null);
            address.setIsDefault(false);
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Mono.just(user));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            AddressResponse response = addressService.addAddress(userId, addressRequest).block();

            // Assert
            assertThat(response.getIsDefault()).isFalse();

            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should add address with null addressLine2")
        void shouldAddAddressWithNullAddressLine2() {
            // Arrange
            addressRequest.setAddressLine2(null);
            address.setAddressLine2(null);
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Mono.just(user));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            AddressResponse response = addressService.addAddress(userId, addressRequest).block();

            // Assert
            assertThat(response.getAddressLine2()).isNull();

            verify(addressRepository).save(any(Address.class));
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
            addressRequest.setCity("Los Angeles");
            addressRequest.setState("CA");
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.just(address));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            AddressResponse response = addressService.updateAddress(userId, addressId, addressRequest).block();

            // Assert
            assertThat(response).isNotNull();
            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFound() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> addressService.updateAddress(userId, addressId, addressRequest).block())
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessage("Address not found");

            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
            verify(addressRepository, never()).save(any(Address.class));
        }

        @Test
        @DisplayName("Should update isDefault when provided")
        void shouldUpdateIsDefaultWhenProvided() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            addressRequest.setIsDefault(false);
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.just(address));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            addressService.updateAddress(userId, addressId, addressRequest).block();

            // Assert
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should not update isDefault when not provided")
        void shouldNotUpdateIsDefaultWhenNotProvided() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            addressRequest.setIsDefault(null);
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.just(address));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            addressService.updateAddress(userId, addressId, addressRequest).block();

            // Assert
            verify(addressRepository).save(any(Address.class));
        }
    }

    @Nested
    @DisplayName("deleteAddress() Tests")
    class DeleteAddressTests {

        @Test
        @DisplayName("Should soft delete address successfully")
        void shouldSoftDeleteAddressSuccessfully() {
            // Arrange
            Long userId = 1L;
            Long addressId = 1L;
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.just(address));
            when(addressRepository.save(any(Address.class))).thenReturn(Mono.just(address));

            // Act
            addressService.deleteAddress(userId, addressId).block();

            // Assert
            assertThat(address.getDeleted()).isTrue();
            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
            verify(addressRepository).save(any(Address.class));
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFoundOnDelete() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> addressService.deleteAddress(userId, addressId).block())
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessage("Address not found");

            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
            verify(addressRepository, never()).save(any(Address.class));
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
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.just(address));

            // Act
            AddressResponse response = addressService.getAddress(userId, addressId).block();

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUserId()).isEqualTo(1L);

            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
        }

        @Test
        @DisplayName("Should throw AddressNotFoundException when address not found")
        void shouldThrowExceptionWhenAddressNotFoundOnGet() {
            // Arrange
            Long userId = 1L;
            Long addressId = 999L;
            when(addressRepository.findByIdAndUserIdAndDeletedFalse(addressId, userId))
                    .thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> addressService.getAddress(userId, addressId).block())
                    .isInstanceOf(AddressNotFoundException.class)
                    .hasMessage("Address not found");

            verify(addressRepository).findByIdAndUserIdAndDeletedFalse(addressId, userId);
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
            Address address2 = new Address();
            address2.setId(2L);
            address2.setUserId(1L);
            address2.setAddressLine1("456 Oak Street");
            address2.setCity("Boston");
            address2.setState("MA");
            address2.setCountry("USA");
            address2.setPostalCode("02101");
            address2.setDeleted(false);
            address2.setCreatedAt(LocalDateTime.now());
            address2.setUpdatedAt(LocalDateTime.now());

            when(userRepository.findById(userId)).thenReturn(Mono.just(user));
            when(addressRepository.findByUserIdAndDeletedFalse(userId))
                    .thenReturn(Flux.just(address, address2));

            // Act
            List<AddressResponse> responses = addressService.getAllAddresses(userId).collectList().block();

            // Assert
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).getId()).isEqualTo(1L);
            assertThat(responses.get(1).getId()).isEqualTo(2L);

            verify(userRepository).findById(userId);
            verify(addressRepository).findByUserIdAndDeletedFalse(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void shouldThrowExceptionWhenUserNotFoundOnGetAll() {
            // Arrange
            Long userId = 999L;
            when(userRepository.findById(userId)).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> addressService.getAllAddresses(userId).collectList().block())
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("User not found with id: " + userId);

            verify(userRepository).findById(userId);
            verify(addressRepository, never()).findByUserIdAndDeletedFalse(anyLong());
        }

        @Test
        @DisplayName("Should return empty list when user has no addresses")
        void shouldReturnEmptyListWhenNoAddresses() {
            // Arrange
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Mono.just(user));
            when(addressRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(Flux.empty());

            // Act
            List<AddressResponse> responses = addressService.getAllAddresses(userId).collectList().block();

            // Assert
            assertThat(responses).isEmpty();

            verify(userRepository).findById(userId);
            verify(addressRepository).findByUserIdAndDeletedFalse(userId);
        }
    }
}
