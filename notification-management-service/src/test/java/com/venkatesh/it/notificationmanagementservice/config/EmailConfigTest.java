package com.venkatesh.it.notificationmanagementservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.mail.host=smtp.example.com",
    "spring.mail.port=587",
    "spring.mail.username=test@example.com",
    "spring.mail.password=testpassword",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true"
})
class EmailConfigTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    void whenContextLoads_thenJavaMailSenderBeanExists() {
        assertNotNull(javaMailSender);
    }

    @Test
    void whenJavaMailSenderBean_thenPropertiesAreSet() {
        assertNotNull(javaMailSender);
        assertTrue(javaMailSender instanceof org.springframework.mail.javamail.JavaMailSenderImpl);
    }
}
