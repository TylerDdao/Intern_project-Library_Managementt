package com.example.library_management.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.text.NumberFormat;

@Component
public class Formatter {
    public String formatDateTime(LocalDateTime dateTime, Locale locale) {

        String pattern;

        if (locale.equals(Locale.US)) {
            pattern = "MM/dd/yyyy h:mm a";
        } else {
            pattern = "dd/MM/yyyy HH:mm";
        }

        return dateTime.format(
                DateTimeFormatter.ofPattern(pattern, locale)
        );
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

    public long calculateDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }
}
