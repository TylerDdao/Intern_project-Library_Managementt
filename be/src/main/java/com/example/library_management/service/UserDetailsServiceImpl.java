package com.example.library_management.service;

import com.example.library_management.model.Feature;
import com.example.library_management.model.User;
import com.example.library_management.repository.FeatureRepository;
import com.example.library_management.repository.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageSource.getMessage("error.username.not.found", null, LocaleContextHolder.getLocale()) + username));
        List<GrantedAuthority>authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(user.getRole().getName()));

        featureRepository.findByRoles_Id(user.getRole().getId()).forEach(feature ->
                authorities.add(new SimpleGrantedAuthority(feature.getName())));

        System.out.println(authorities);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}