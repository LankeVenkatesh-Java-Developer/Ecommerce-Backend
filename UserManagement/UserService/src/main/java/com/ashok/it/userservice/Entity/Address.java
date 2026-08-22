package com.ashok.it.userservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    private Long id;

    private Long userId;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String addressType;

    private Boolean isDefault = false;

    private Boolean deleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}