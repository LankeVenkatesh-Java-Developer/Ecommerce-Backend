package com.ashok.it.userservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String mobileNumber;

    private String password;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}