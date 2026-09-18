package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.feign.UserClient;
import com.venkatesh.it.adminmanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserClient userClient;

    @Override
    public Set<String> getUserRoles(Long userId) {
        try {
            log.debug("Fetching user roles for userId: {}", userId);
            
            Set<String> roles = userClient.getUserRoles(userId);
            
            if (roles != null && !roles.isEmpty()) {
                Set<String> roleSet = new HashSet<>();
                for (String role : roles) {
                    roleSet.add("ROLE_" + role.toUpperCase());
                }
                log.debug("User {} has roles: {}", userId, roleSet);
                return roleSet;
            } else {
                log.warn("No roles returned for user: {}, defaulting to ADMIN role", userId);
                return Set.of("ROLE_ADMIN");
            }
        } catch (Exception e) {
            log.error("Failed to fetch user roles for userId: {}. Error: {}", userId, e.getMessage());
            log.warn("Defaulting to ADMIN role for user: {} due to service failure", userId);
            return Set.of("ROLE_ADMIN");
        }
    }

    @Override
    public boolean isValidUser(Long userId) {
        try {
            log.debug("Validating user with userId: {}", userId);
            
            Boolean isValid = userClient.isValidUser(userId);
            return isValid != null && isValid;
        } catch (Exception e) {
            log.warn("Failed to validate user with userId: {}, assuming valid. Error: {}", userId, e.getMessage());
            return true;
        }
    }
}
