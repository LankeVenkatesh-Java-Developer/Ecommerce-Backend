package com.ashok.it.userservice.Event;

import com.ashok.it.userservice.Config.KafkaConfig;
import com.ashok.it.userservice.Dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<Void> publishUserCreatedEvent(UserResponse userResponse) {
        return Mono.fromRunnable(() -> {
            UserEvent event = new UserEvent();
            event.setEventType("USER_CREATED");
            event.setUserId(userResponse.getId());
            event.setEmail(userResponse.getEmail());
            event.setTimestamp(LocalDateTime.now());
            
            kafkaTemplate.send(KafkaConfig.USER_TOPIC, event);
            
            log.info("Published USER_CREATED event for user: {}", userResponse.getEmail());
        }).then();
    }

    public Mono<Void> publishUserUpdatedEvent(UserResponse userResponse) {
        return Mono.fromRunnable(() -> {
            UserEvent event = new UserEvent();
            event.setEventType("USER_UPDATED");
            event.setUserId(userResponse.getId());
            event.setEmail(userResponse.getEmail());
            event.setTimestamp(LocalDateTime.now());
            
            kafkaTemplate.send(KafkaConfig.USER_TOPIC, event);
            
            log.info("Published USER_UPDATED event for user: {}", userResponse.getEmail());
        }).then();
    }
}
