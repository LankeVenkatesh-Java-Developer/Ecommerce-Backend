package com.ashok.it.userservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String type;
    private String recipient;
    private String status;
    private String message;
    private LocalDateTime sentAt;
}
