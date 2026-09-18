package com.venkatesh.it.usermanagementservice.repository;

import com.venkatesh.it.usermanagementservice.model.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

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
    }

    @Test
    void whenSaveUser_thenReturnSavedUser() {
        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getFirstName(), savedUser.getFirstName());
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        entityManager.persist(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void whenFindByEmailNotExists_thenReturnEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void whenFindByMobileNumber_thenReturnUser() {
        entityManager.persist(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByMobileNumber("+1234567890");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getMobileNumber(), foundUser.get().getMobileNumber());
    }

    @Test
    void whenExistsByEmail_thenReturnTrue() {
        entityManager.persist(testUser);
        entityManager.flush();

        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        assertTrue(exists);
    }

    @Test
    void whenExistsByEmailNotExists_thenReturnFalse() {
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void whenExistsByMobileNumber_thenReturnTrue() {
        entityManager.persist(testUser);
        entityManager.flush();

        boolean exists = userRepository.existsByMobileNumber("+1234567890");

        assertTrue(exists);
    }

    @Test
    void whenFindByStatus_thenReturnUsers() {
        entityManager.persist(testUser);
        entityManager.flush();

        List<User> users = userRepository.findByStatus(UserStatus.ACTIVE);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void whenFindByRole_thenReturnUsers() {
        entityManager.persist(testUser);
        entityManager.flush();

        List<User> users = userRepository.findByRole(UserRole.CUSTOMER);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void whenFindByStatusAndRole_thenReturnUsers() {
        entityManager.persist(testUser);
        entityManager.flush();

        List<User> users = userRepository.findByStatusAndRole(UserStatus.ACTIVE, UserRole.CUSTOMER);

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void whenFindByEmailOrMobileNumber_thenReturnUser() {
        entityManager.persist(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByEmailOrMobileNumber("john.doe@example.com", "+1234567890");

        assertTrue(foundUser.isPresent());
    }

    @Test
    void whenSearchByKeyword_thenReturnUsers() {
        entityManager.persist(testUser);
        entityManager.flush();

        List<User> users = userRepository.searchByKeyword("John");

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }

    @Test
    void whenDeleteById_thenUserDeleted() {
        User savedUser = entityManager.persist(testUser);
        entityManager.flush();

        userRepository.deleteById(savedUser.getId());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}
