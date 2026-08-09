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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Page<BorrowResponse> getMyBorrows(int page, int limit, String sortBy, String sortDir, boolean isActive, Long bookId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(bookId == null){
            Page<Borrow> borrows = borrowRepository.findByIsActiveAndUserUsername(isActive, username, pageable);
            return borrows.map(BorrowResponse::new);
        }
        else{
            Page<Borrow> borrows = borrowRepository.findByIsActiveAndBookId(isActive, bookId, pageable);
            return borrows.map(BorrowResponse::new);
        }
    }

    public Page<BorrowResponse> getBorrows(int page, int limit, String sortBy, String sortDir, String searchQuery) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime fiveDaysOutEnd = todayStart.plusDays(6).minusNanos(1);

        boolean hasSearch = searchQuery != null && !searchQuery.isBlank();

        List<String> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();

        Page<Borrow> borrows;
        boolean isAdmin = authorities.stream().anyMatch(a -> a.equals("GET_BORROW_MULTI") || a.equals("ROLE_ROOT"));

        if (!isAdmin) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            borrows = hasSearch
                    ? borrowRepository.findBySearchQueryAndUsername(searchQuery, username, pageable)
                    : borrowRepository.findByUserUsername(username, pageable);
        } else {
            borrows = hasSearch
                    ? borrowRepository.findBySearchQuery(searchQuery, pageable)
                    : borrowRepository.findByIsActive(true, pageable);
        }

        return borrows.map(BorrowResponse::new);
    }

    public Page<BorrowResponse> getBorrowsByStatus(int page, int limit, String sortBy, String sortDir, String status) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime fiveDaysOutEnd = todayStart.plusDays(6).minusNanos(1);

        List<String> authorities = SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();

        boolean isAdmin = authorities.stream().anyMatch(a -> a.equals("GET_BORROW_MULTI") || a.equals("ROLE_ROOT"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<Borrow> borrows;

        if (!isAdmin) {
            borrows = switch (status.toLowerCase().trim()) {
                case "late" -> borrowRepository.findByUserUsernameAndDueDateLessThanAndIsActiveTrue(username, todayStart, pageable);
                case "near" -> borrowRepository.findByUserUsernameAndDueDateBetweenAndIsActiveTrue(username, todayStart, fiveDaysOutEnd, pageable);
                case "returned" -> borrowRepository.findByUserUsernameAndIsActive(username, false, pageable);
                default -> borrowRepository.findByUserUsernameAndDueDateGreaterThanAndIsActiveTrue(username, fiveDaysOutEnd, pageable);
            };
        } else {
            borrows = switch (status.toLowerCase().trim()) {
                case "late" -> borrowRepository.findByDueDateLessThanAndIsActiveTrue(todayStart, pageable);
                case "near" -> borrowRepository.findByDueDateBetweenAndIsActiveTrue(todayStart, fiveDaysOutEnd, pageable);
                case "returned" -> borrowRepository.findByIsActive(false, pageable);
                default -> borrowRepository.findByDueDateGreaterThanAndIsActiveTrue(fiveDaysOutEnd, pageable);
            };
        }

        return borrows.map(BorrowResponse::new);
    }

    public Map<String, Long> getBorrowCountsByGenre() {
        List<Object[]> results = borrowRepository.countBorrowsByGenreAndIsActiveTrue();
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : results) {
            map.put((String) row[0], (Long) row[1]);
        }
        return map;
    }
}
