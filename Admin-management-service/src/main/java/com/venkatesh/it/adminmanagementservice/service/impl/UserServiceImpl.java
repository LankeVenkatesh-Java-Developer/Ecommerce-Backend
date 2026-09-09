package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;
    
    @Value("${user.service.base-url:http://localhost:8081/api/users}")
    private String userServiceBaseUrl;
    
    @Value("${user.service.enabled:false}")
    private boolean userServiceEnabled;

    @Override
    public Set<String> getUserRoles(Long userId) {
        if (!userServiceEnabled) {
            log.info("User service integration disabled, defaulting to ADMIN role for user: {}", userId);
            return Set.of("ROLE_ADMIN", "ROLE_SUPER_ADMIN");
        }
        
        try {
            String url = userServiceBaseUrl + "/" + userId + "/roles";
            log.debug("Fetching user roles from: {}", url);
            
            String[] roles = restTemplate.getForObject(url, String[].class);
            
            if (roles != null) {
                Set<String> roleSet = new HashSet<>();
                for (String role : roles) {
                    roleSet.add("ROLE_" + role.toUpperCase());
                }
                log.debug("User {} has roles: {}", userId, roleSet);
                return roleSet;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user roles for userId: {}, defaulting to ADMIN role. Error: {}", userId, e.getMessage());
        }
        
        log.info("Defaulting to ADMIN and SUPER_ADMIN roles for user: {}", userId);
        return Set.of("ROLE_ADMIN", "ROLE_SUPER_ADMIN");
    }

    @Override
    public boolean isValidUser(Long userId) {
        if (!userServiceEnabled) {
            log.info("User service integration disabled, assuming user is valid: {}", userId);
            return true;
        }
        
        try {
            String url = userServiceBaseUrl + "/" + userId;
            log.debug("Validating user from: {}", url);
            
            Boolean isValid = restTemplate.getForObject(url, Boolean.class);
            return isValid != null && isValid;
        } catch (Exception e) {
            log.warn("Failed to validate user with userId: {}, assuming valid. Error: {}", userId, e.getMessage());
            return true;
        }
    }
}
