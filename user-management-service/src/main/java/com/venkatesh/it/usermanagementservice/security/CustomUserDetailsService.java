package com.venkatesh.it.usermanagementservice.security;

import com.venkatesh.it.usermanagementservice.model.User;
import com.venkatesh.it.usermanagementservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrMobile) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(usernameOrMobile)
                .orElseGet(() -> userRepository.findByMobileNumber(usernameOrMobile)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email or mobile number: " + usernameOrMobile)));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return UserPrincipal.create(user);
    }
}
