package com.example.library_management.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.NumberFormat;

@Component
public class Formatter {
    public String formatDateTime(LocalDateTime dateTime) {
        Locale locale = LocaleContextHolder.getLocale();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", locale);

        return dateTime.format(formatter);
    }

    public String formatVND(Number amount) {
        if (amount == null) {
            return null;
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("vi-VN")
        );

        return formatter.format(amount);
    }
}
