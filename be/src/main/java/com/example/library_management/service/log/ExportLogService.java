package com.example.library_management.service.log;

import com.example.library_management.exception.ApiException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.example.library_management.util.AuditLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExportLogService {

    @Value("${LOG_PATH:logs}")
    private String logPath;

    @Value("${TZ:}")
    private String tzEnv;

    @Autowired
    private AuditLogger logger;

    @Autowired
    private MessageSource messageSource;

    private static final DateTimeFormatter INPUT_TS = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter OUTPUT_TS = DateTimeFormatter.ofPattern("dd/MM/yyyy @ HH:mm:ss");

    // Matches: 2026-08-12T10:23:07.934+07:00  INFO  c.e.l.util.AuditLogger : rest-of-message
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+)\\s+(\\w+)\\s+\\S+\\s*:\\s*(.*)$"
    );

    // Matches an author bracket at the start of the message, e.g. [SYSTEM] or [tyler]
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("^\\[([^\\]]+)\\]\\s*(.*)$");

    private record LogEntry(String time, String type, String author, String content) {}

    /**
     * Resolves a human-readable timezone label from the TZ environment variable
     * (e.g. "Asia/Ho_Chi_Minh" -> "GMT+7"). Falls back to "GMT+7" if TZ is unset
     * or can't be parsed as a valid zone id.
     */
    private String resolveTimezoneLabel() {
        if (tzEnv == null || tzEnv.isBlank()) {
            return "GMT+7 Asia/Ho Chi Minh";
        }
        try {
            ZoneId zoneId = ZoneId.of(tzEnv);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            String offset = now.getOffset().getId(); // e.g. "+07:00" or "Z"
            if ("Z".equals(offset)) {
                offset = "+00:00";
            }
            // Trim ":00" minutes when whole-hour, e.g. "+07:00" -> "+7"
            String sign = offset.substring(0, 1);
            String[] parts = offset.substring(1).split(":");
            int hours = Integer.parseInt(parts[0]);
            String minutes = parts.length > 1 ? parts[1] : "00";
            String hourPart = sign + hours;
            String readableZone = tzEnv.replace('_', ' ');
            return minutes.equals("00")
                    ? "GMT" + hourPart + "  " + readableZone
                    : "GMT" + hourPart + ":" + minutes + "  " + readableZone;
        } catch (Exception e) {
            return "GMT+7 Asia/Ho Chi Minh";
        }
    }

    /**
     * Exports logs for a single day to an Excel file.
     */
    public ByteArrayInputStream logsToExcel(LocalDate date) {
        return logsToExcel(date, date);
    }

    /**
     * Exports logs for a date range (inclusive) to an Excel file.
     * For each day: if it's today, reads the live app-logs.log file.
     * Otherwise, reads the rotated app-logs-yyyy-MM-dd.log file.
     */
    public ByteArrayInputStream logsToExcel(LocalDate from, LocalDate to) {
        List<LogEntry> entries = new ArrayList<>();
        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            entries.addAll(readLogFile(day));
        }

        if(entries.isEmpty()){
            throw new ApiException(HttpStatus.NOT_FOUND, "LOG-FILE-NOT-FOUND", messageSource.getMessage("error.log.not.found", null, LocaleContextHolder.getLocale()));
        }

        String rangeLabel = from.equals(to) ? from.toString() : from + " to " + to;
        String[] headers = {"Timestamp", "Type", "Author", "Log Content"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Logs");

            CellStyle warningStyle = workbook.createCellStyle();
            warningStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            warningStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle errorStyle = workbook.createCellStyle();
            errorStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Row 0: timezone note
            Row noteRow = sheet.createRow(0);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue("Timezone: " + resolveTimezoneLabel() + " | Date: " + rangeLabel);
            CellStyle noteStyle = workbook.createCellStyle();
            Font italicFont = workbook.createFont();
            italicFont.setItalic(true);
            noteStyle.setFont(italicFont);
            noteCell.setCellStyle(noteStyle);

            // Row 1: headers
            Row headerRow = sheet.createRow(1);
            CellStyle headerStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            headerStyle.setFont(boldFont);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows starting at row 2
            int rowIdx = 2;
            for (LogEntry entry : entries) {
                Row row = sheet.createRow(rowIdx++);

                CellStyle rowStyle = null;
                if ("WARN".equalsIgnoreCase(entry.type())) {
                    rowStyle = warningStyle;
                } else if ("ERROR".equalsIgnoreCase(entry.type())) {
                    rowStyle = errorStyle;
                }

                Cell c0 = row.createCell(0);
                c0.setCellValue(entry.time());
                Cell c1 = row.createCell(1);
                c1.setCellValue(entry.type());
                Cell c2 = row.createCell(2);
                c2.setCellValue(entry.author());
                Cell c3 = row.createCell(3);
                c3.setCellValue(entry.content());

                if (rowStyle != null) {
                    c0.setCellStyle(rowStyle);
                    c1.setCellStyle(rowStyle);
                    c2.setCellStyle(rowStyle);
                    c3.setCellStyle(rowStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            logger.log("Exported log file for range {}", rangeLabel);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage("error.failed.to.export", null, LocaleContextHolder.getLocale()) + ": " + e.getMessage());
        }
    }

    private List<LogEntry> readLogFile(LocalDate date) {
        List<LogEntry> entries = new ArrayList<>();

        String fileName = date.isEqual(LocalDate.now())
                ? "app-logs.log"
                : "app-logs-" + date + ".log";

        Path path = Path.of(logPath, fileName);

        if (!Files.exists(path)) {
            return entries;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            LogEntry lastEntry = null;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String rawTimestamp = matcher.group(1);
                    String level = matcher.group(2);
                    String rest = matcher.group(3);

                    // Only keep lines that fall on the requested day
                    LocalDate lineDate;
                    String formattedTime;
                    try {
                        OffsetDateTime odt = OffsetDateTime.parse(rawTimestamp, INPUT_TS);
                        lineDate = odt.toLocalDate();
                        formattedTime = odt.format(OUTPUT_TS);
                    } catch (Exception ex) {
                        lastEntry = null;
                        continue;
                    }

                    if (!lineDate.isEqual(date)) {
                        lastEntry = null;
                        continue;
                    }

                    String author = "";
                    String content = rest;
                    Matcher authorMatcher = AUTHOR_PATTERN.matcher(rest);
                    if (authorMatcher.matches()) {
                        author = authorMatcher.group(1);
                        content = authorMatcher.group(2);
                    }

                    lastEntry = new LogEntry(formattedTime, level, author, content);
                    entries.add(lastEntry);
                } else if (lastEntry != null && !line.isBlank()) {
                    // Continuation line (e.g. stack trace) — append to previous entry's content
                    int lastIdx = entries.size() - 1;
                    LogEntry prev = entries.get(lastIdx);
                    entries.set(lastIdx, new LogEntry(prev.time(), prev.type(), prev.author(),
                            prev.content() + " | " + line.trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read log file: " + e.getMessage());
        }

        return entries;
    }
}