package com.venkatesh.it.notificationmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotificationManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationManagementServiceApplication.class, args);
    }

}
