package com.example.library_management.service;

import com.example.library_management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    // store token → expiration time
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtil jwtUtil;

    public void blacklist(String token) {
        Date expiration = jwtUtil.extractExpiration(token);
        blacklistedTokens.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    @Scheduled(fixedRate = 3600000)
    public void removeExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
        System.out.println("Cleaned blacklist, remaining: " + blacklistedTokens.size());
    }
}
