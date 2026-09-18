package com.venkatesh.it.usermanagementservice.repository;

import com.venkatesh.it.usermanagementservice.model.Address;
import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.model.enums.AddressType;
import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("+1234567890")
                .password("encodedPassword")
                .status(UserStatus.ACTIVE)
                .role(UserRole.CUSTOMER)
                .build();
        testUser = entityManager.persist(testUser);

        testAddress = Address.builder()
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
    }

    @Test
    void whenSaveAddress_thenReturnSavedAddress() {
        Address savedAddress = addressRepository.save(testAddress);

        assertNotNull(savedAddress);
        assertNotNull(savedAddress.getId());
        assertEquals(testAddress.getAddressLine1(), savedAddress.getAddressLine1());
        assertEquals(testAddress.getCity(), savedAddress.getCity());
    }

    @Test
    void whenFindByUserId_thenReturnAddresses() {
        entityManager.persist(testAddress);
        entityManager.flush();

        List<Address> addresses = addressRepository.findByUserId(testUser.getId());

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
    }

    @Test
    void whenFindByIdAndUserId_thenReturnAddress() {
        Address savedAddress = entityManager.persist(testAddress);
        entityManager.flush();

        Optional<Address> foundAddress = addressRepository.findByIdAndUserId(savedAddress.getId(), testUser.getId());

        assertTrue(foundAddress.isPresent());
        assertEquals(savedAddress.getId(), foundAddress.get().getId());
    }

    @Test
    void whenFindByUserIdAndAddressType_thenReturnAddresses() {
        entityManager.persist(testAddress);
        entityManager.flush();

        List<Address> addresses = addressRepository.findByUserIdAndAddressType(testUser.getId(), AddressType.HOME);

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
    }

    @Test
    void whenFindByUserIdAndIsDefaultTrue_thenReturnDefaultAddress() {
        testAddress.setIsDefault(true);
        Address savedAddress = entityManager.persist(testAddress);
        entityManager.flush();

        Optional<Address> defaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(testUser.getId());

        assertTrue(defaultAddress.isPresent());
        assertTrue(defaultAddress.get().getIsDefault());
    }

    @Test
    void whenHasDefaultAddress_thenReturnTrue() {
        testAddress.setIsDefault(true);
        entityManager.persist(testAddress);
        entityManager.flush();

        boolean hasDefault = addressRepository.hasDefaultAddress(testUser.getId());

        assertTrue(hasDefault);
    }

    @Test
    void whenHasDefaultAddressNoDefault_thenReturnFalse() {
        entityManager.persist(testAddress);
        entityManager.flush();

        boolean hasDefault = addressRepository.hasDefaultAddress(testUser.getId());

        assertFalse(hasDefault);
    }

    @Test
    void whenUnsetDefaultAddressForUser_thenOtherAddressesUnset() {
        Address address1 = entityManager.persist(testAddress);
        
        Address address2 = Address.builder()
                .user(testUser)
                .addressLine1("456 Oak Ave")
                .city("Los Angeles")
                .state("CA")
                .country("USA")
                .postalCode("90001")
                .addressType(AddressType.WORK)
                .isDefault(true)
                .build();
        Address savedAddress2 = entityManager.persist(address2);
        entityManager.flush();

        addressRepository.unsetDefaultAddressForUser(testUser.getId(), savedAddress2.getId());
        entityManager.flush();

        Optional<Address> address1After = addressRepository.findById(address1.getId());
        assertTrue(address1After.isPresent());
        assertFalse(address1After.get().getIsDefault());
    }

    @Test
    void whenDeleteById_thenAddressDeleted() {
        Address savedAddress = entityManager.persist(testAddress);
        entityManager.flush();

        addressRepository.deleteById(savedAddress.getId());

        Optional<Address> deletedAddress = addressRepository.findById(savedAddress.getId());
        assertFalse(deletedAddress.isPresent());
    }
}
