package com.example.library_management.service.announcement;

import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import com.example.library_management.model.Announcement;
import com.example.library_management.model.AnnouncementType;
import com.example.library_management.repository.AnnouncementRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GetAnnouncementService {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AnnouncementRepository announcementRepository;

    public Page<AnnouncementResponse> getAnnouncements(int page, int limit, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, limit, sort);
        Page<Announcement> announcements;
        announcements = announcementRepository.findAll(pageable);
        return announcements.map(AnnouncementResponse::new);
    }
}
