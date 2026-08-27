package com.venkatesh.it.usermanagementservice.model.dto;

import com.venkatesh.it.usermanagementservice.model.enums.UserRole;
import com.venkatesh.it.usermanagementservice.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AddressResponse> addresses;
}
