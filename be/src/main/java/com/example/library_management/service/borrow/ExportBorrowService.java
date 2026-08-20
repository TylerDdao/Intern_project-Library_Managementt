package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.borrow.BorrowRequest;
import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExportBorrowService {
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MessageSource messageSource;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy @ HH:mm:ss");

    public ByteArrayInputStream borrowsToExcel(List<BorrowRequest> requests) {
        List<Borrow> borrows = new ArrayList<>();
        requests.forEach(request -> {
            borrowRepository.findById(request.getId()).ifPresent(borrows::add);
        });
        String[] headers = {"ID", "Username", "Full name", "Email", "Phone number", "Title", "Author", "Borrowed on", "Due on", "Penalty"};

        NumberFormat vndFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Borrows");

            // Row 0: timezone note
            Row noteRow = sheet.createRow(0);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue("Timezone: GMT+7 (Asia/Ho_Chi_Minh)");
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
            for (Borrow borrow : borrows) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(borrow.getId());
                row.createCell(1).setCellValue(borrow.getUser().getUsername());
                row.createCell(2).setCellValue(borrow.getUser().getFullName());
                row.createCell(3).setCellValue(borrow.getUser().getEmail());
                row.createCell(4).setCellValue(borrow.getUser().getPhoneNumber());
                row.createCell(5).setCellValue(borrow.getBook().getTitle());
                row.createCell(6).setCellValue(borrow.getBook().getAuthor());
                row.createCell(7).setCellValue(formatDate(borrow.getCreatedAt()));
                row.createCell(8).setCellValue(formatDate(borrow.getDueDate()));

                Float penalty = borrow.getPenalty();
                row.createCell(9).setCellValue(penalty != null ? vndFormat.format(penalty) : "0 ₫");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            String message = messageSource.getMessage("error.export.failed", null, LocaleContextHolder.getLocale());
            throw new RuntimeException(message + ": " + e.getMessage());
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}