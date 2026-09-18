package com.venkatesh.it.productsmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProductsManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductsManagementServiceApplication.class, args);
    }

}
