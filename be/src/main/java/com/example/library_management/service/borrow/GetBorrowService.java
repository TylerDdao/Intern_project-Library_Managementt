package com.example.library_management.service.borrow;

import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.model.Book;
import com.example.library_management.model.Borrow;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class GetBorrowService {
    @Autowired
    private AuditLogger logger;

    @Autowired
    private BorrowRepository borrowRepository;

    public Page<BorrowResponse> getBorrows(int page, int limit, String sortBy, String sortDir, Long userId, String searchQuery){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Borrow> borrows = userId != null
                ? borrowRepository.findByUserId(userId, pageable)
                : borrowRepository.findAll(pageable);

        return borrows.map(BorrowResponse::new);
    }

    public Page<BorrowResponse> getBorrowsByStatus(int page, int limit, String sortBy, String sortDir, String status) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        // Normalize to today at midnight (00:00:00)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime fiveDaysOutEnd = todayStart.plusDays(6).minusNanos(1); // End of the 5th day

        Page<Borrow> borrows;

        switch (status.toLowerCase().trim()) {
            case "late" ->
                borrows = borrowRepository.findByDueDateLessThanAndIsActiveTrue(todayStart, pageable);

            case "near" ->
                borrows = borrowRepository.findByDueDateBetweenAndIsActiveTrue(todayStart, fiveDaysOutEnd, pageable);

            case "returned" ->
                borrows = borrowRepository.findByIsActive(false, pageable);

//            case "borrowing" ->
//                borrows = borrowRepository.findByDueDateGreaterThanAndIsActiveTrue(fiveDaysOutEnd, pageable);

            default ->
                borrows = borrowRepository.findByDueDateGreaterThanAndIsActiveTrue(fiveDaysOutEnd, pageable);
        }

        return borrows.map(BorrowResponse::new);
    }

    public Map<String, Long> getBorrowCountsByGenre() {
        List<Object[]> results = borrowRepository.countBorrowsByGenre();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }
}
