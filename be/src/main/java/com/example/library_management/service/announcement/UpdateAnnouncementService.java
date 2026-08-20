package com.example.library_management.service.announcement;

import com.example.library_management.dto.request.announcement.AnnouncementRequest;
import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import com.example.library_management.exception.ApiException;
import com.example.library_management.model.Announcement;
import com.example.library_management.repository.AnnouncementRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UpdateAnnouncementService {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AuditLogger logger;
    @Autowired
    private AnnouncementRepository announcementRepository;

    public AnnouncementResponse updateAnnouncement(AnnouncementRequest request){
        Announcement announcement = announcementRepository.findById(request.getId()).orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND, "ANNOUNCEMENT-NOT-FOUND", messageSource.getMessage("error.announcement.not.found", null, LocaleContextHolder.getLocale())));
        announcement.setIsActive(request.getIsActive());
        Announcement saved = announcementRepository.save(announcement);
        logger.log("Updated announcement {} -> {}, ID#{}", announcement.getIsActive(), saved.getIsActive(), saved.getId());
        return new AnnouncementResponse(announcement);
    }
}
