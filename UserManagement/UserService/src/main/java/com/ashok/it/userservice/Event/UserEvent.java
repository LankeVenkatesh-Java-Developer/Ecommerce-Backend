package com.ashok.it.userservice.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String eventType;
    private Long userId;
    private String email;
    private LocalDateTime timestamp;
}
