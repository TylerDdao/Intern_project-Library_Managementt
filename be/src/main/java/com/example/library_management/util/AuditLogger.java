package com.example.library_management.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogger {
    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    public void log(String actor, String message, Object... args) {
        String formatted = MessageFormatter.arrayFormat(message, args).getMessage();
        log.info("[{}] {}", actor, formatted);
    }

    public void log(String message, Object... args) {
        String user = getCurrentUser();
        String formatted = MessageFormatter.arrayFormat(message, args).getMessage();
        log.info("[{}] {}", user, formatted);
    }

    public void error(String message, Object... args){
        String user = getCurrentUser();
        String formatted = MessageFormatter.arrayFormat(message, args).getMessage();
        log.error("[{}] {}", user, formatted);
    }

    private Object[] prepend(Object first, Object[] rest) {
        Object[] result = new Object[rest.length + 1];
        result[0] = first;
        System.arraycopy(rest, 0, result, 1, rest.length);
        return result;
    }
}
