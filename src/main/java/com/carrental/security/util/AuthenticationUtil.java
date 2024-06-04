package com.carrental.security.util;

import com.carrental.model.User;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    public boolean isManager() {
        return getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));
    }

    public User getCurrentUserFromDb() {
        String username = ((UserDetails) getAuthentication()
                .getPrincipal())
                .getUsername();

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }
}
