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


@Service
public class GetBorrowService {
    @Autowired
    private AuditLogger logger;

    @Autowired
    private BorrowRepository borrowRepository;

    public Page<BorrowResponse> getBorrows(int page, int limit, String sortBy, String sortDir, Long userId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Borrow> borrows = userId != null
                ? borrowRepository.findByUserId(userId, pageable)
                : borrowRepository.findAll(pageable);

        return borrows.map(BorrowResponse::new);
    }

    public Page<BorrowResponse> getNearestBorrowByBookId(int page, int limit, String sortBy, String sortDir, Long bookId){
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);

        Page<Borrow> borrows = borrowRepository.findByBookId(bookId, pageable);

        return borrows.map(BorrowResponse::new);
    }
}
