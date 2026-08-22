package com.ashok.it.userservice.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    private Long id;

    private Long userId;

    private String tokenHash;

    private LocalDateTime expiresAt;

    private Boolean used = false;

    private LocalDateTime createdAt;
}
