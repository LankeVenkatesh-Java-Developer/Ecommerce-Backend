package com.ashok.it.userservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String type;
    private String recipient;
    private Long userId;
    private String subject;
    private String message;
}
