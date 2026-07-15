package com.example.library_management.service.borrow;

import com.example.library_management.dto.request.BorrowRequest;
import com.example.library_management.dto.response.BorrowResponse;
import com.example.library_management.dto.response.RoleResponse;
import com.example.library_management.model.Borrow;
import com.example.library_management.model.Feature;
import com.example.library_management.model.Role;
import com.example.library_management.repository.BorrowRepository;
import com.example.library_management.util.AuditLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UpdateBorrowService {
    @Autowired
    BorrowRepository borrowRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuditLogger logger;

    public BorrowResponse updateBorrow(BorrowRequest request){
        Borrow borrow = borrowRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("error.borrow.not.found", null, LocaleContextHolder.getLocale())
                ));

        if (request.getDueDate() != null) {
            borrow.setDueDate(request.getDueDate());
        }
        if (request.getIsActive() != null) {
            borrow.setIsActive(request.getIsActive());
        }

        borrowRepository.save(borrow);

        logger.log("Updated borrow ID #{}", borrow.getId());
        return new BorrowResponse(borrow);
    }
}
