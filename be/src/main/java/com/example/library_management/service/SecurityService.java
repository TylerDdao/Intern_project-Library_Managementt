package com.example.library_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    public boolean hasAccess(String requiredAuthority) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;
        boolean isRoot = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ROOT"));
        if (isRoot){
            return true;
        };

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(requiredAuthority));
    }
}