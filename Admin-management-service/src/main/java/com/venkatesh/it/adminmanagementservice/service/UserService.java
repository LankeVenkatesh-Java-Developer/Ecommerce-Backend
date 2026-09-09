package com.venkatesh.it.adminmanagementservice.service;

import java.util.Set;

public interface UserService {
    
    Set<String> getUserRoles(Long userId);
    
    boolean isValidUser(Long userId);
}
