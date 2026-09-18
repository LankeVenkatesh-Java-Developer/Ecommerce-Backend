package com.venkatesh.it.usermanagementservice.repository;

import com.venkatesh.it.usermanagementservice.model.Otp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Otp testOtp;

    @BeforeEach
    void setUp() {
        testOtp = Otp.builder()
                .email("test@example.com")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();
    }

    @Test
    void whenSaveOtp_thenReturnSavedOtp() {
        Otp savedOtp = otpRepository.save(testOtp);

        assertNotNull(savedOtp);
        assertNotNull(savedOtp.getId());
        assertEquals(testOtp.getEmail(), savedOtp.getEmail());
        assertEquals(testOtp.getOtpCode(), savedOtp.getOtpCode());
    }

    @Test
    void whenFindByEmailAndOtpCodeAndIsUsedFalse_thenReturnOtp() {
        entityManager.persist(testOtp);
        entityManager.flush();

        Optional<Otp> foundOtp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@example.com", "123456");

        assertTrue(foundOtp.isPresent());
        assertEquals(testOtp.getEmail(), foundOtp.get().getEmail());
        assertEquals(testOtp.getOtpCode(), foundOtp.get().getOtpCode());
    }

    @Test
    void whenFindByEmailAndOtpCodeAndIsUsedFalseNotExists_thenReturnEmpty() {
        Optional<Otp> foundOtp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@example.com", "000000");

        assertFalse(foundOtp.isPresent());
    }

    @Test
    void whenFindTopByEmailOrderByCreatedAtDesc_thenReturnLatestOtp() {
        Otp otp1 = entityManager.persist(testOtp);
        
        Otp otp2 = Otp.builder()
                .email("test@example.com")
                .otpCode("654321")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isUsed(false)
                .build();
        entityManager.persist(otp2);
        entityManager.flush();

        Optional<Otp> latestOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc("test@example.com");

        assertTrue(latestOtp.isPresent());
        assertEquals(otp2.getOtpCode(), latestOtp.get().getOtpCode());
    }

    @Test
    void whenDeleteByEmail_thenOtpDeleted() {
        entityManager.persist(testOtp);
        entityManager.flush();

        otpRepository.deleteByEmail("test@example.com");

        Optional<Otp> deletedOtp = otpRepository.findByEmailAndOtpCodeAndIsUsedFalse("test@example.com", "123456");
        assertFalse(deletedOtp.isPresent());
    }

    @Test
    void whenDeleteById_thenOtpDeleted() {
        Otp savedOtp = entityManager.persist(testOtp);
        entityManager.flush();

        otpRepository.deleteById(savedOtp.getId());

        Optional<Otp> deletedOtp = otpRepository.findById(savedOtp.getId());
        assertFalse(deletedOtp.isPresent());
    }
}
