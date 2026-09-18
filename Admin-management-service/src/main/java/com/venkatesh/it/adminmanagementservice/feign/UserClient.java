package com.venkatesh.it.adminmanagementservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient(name = "user-management-service")
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}/roles")
    Set<String> getUserRoles(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/users/{userId}")
    Boolean isValidUser(@PathVariable("userId") Long userId);
}
