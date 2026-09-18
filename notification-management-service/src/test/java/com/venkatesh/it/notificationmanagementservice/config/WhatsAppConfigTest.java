package com.venkatesh.it.notificationmanagementservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "twilio.account.sid=ACtest123",
    "twilio.auth.token=testtoken123",
    "twilio.whatsapp.from.number=+14155238886"
})
class WhatsAppConfigTest {

    @Autowired
    private WhatsAppConfig whatsAppConfig;

    @Test
    void whenContextLoads_thenWhatsAppConfigBeanExists() {
        assertNotNull(whatsAppConfig);
    }

    @Test
    void whenWhatsAppConfigBean_thenPropertiesAreSet() {
        assertNotNull(whatsAppConfig);
        assertEquals("+14155238886", whatsAppConfig.getFromNumber());
    }

    @Test
    void whenGetFromNumber_thenReturnsCorrectValue() {
        String fromNumber = whatsAppConfig.getFromNumber();
        assertNotNull(fromNumber);
        assertEquals("+14155238886", fromNumber);
    }
}
