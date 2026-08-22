package com.ashok.it.userservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String email;
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
}
