package com.example.library_management.service.user;

import com.example.library_management.dto.request.book.BookRequest;
import com.example.library_management.model.Book;
import com.example.library_management.model.Genre;
import com.example.library_management.repository.BookRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportBookService {

    @Autowired
    private BookRepository bookRepository;

    public ByteArrayInputStream booksToExcel(List<BookRequest> requests) {
        List<Book> books = new ArrayList<>();
        requests.forEach(request -> {
            bookRepository.findById(request.getId()).ifPresent(books::add);
        });
        String[] headers = {"ID", "Title", "Author", "Genres", "Copies"};

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Books");

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(book.getId());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getAuthor());
                row.createCell(3).setCellValue(
                        book.getGenres().stream()
                                .map(Genre::getName)
                                .collect(Collectors.joining(", "))
                );
                row.createCell(4).setCellValue(book.getCopies());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel: " + e.getMessage());
        }
    }
}