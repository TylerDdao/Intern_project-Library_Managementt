package com.example.library_management.service.announcement;

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
public class DeleteAnnouncementService {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AuditLogger logger;
    @Autowired
    private AnnouncementRepository announcementRepository;

    public String deleteAnnouncement(Long id){
        Announcement announcement = announcementRepository.findById(id).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND, "ANNOUNCEMENT-NOT-FOUND", messageSource.getMessage("error.announcement.not.found", null, LocaleContextHolder.getLocale())));
        announcementRepository.delete(announcement);
        logger.log("Deleted announcement ID#{}", announcement.getId());
        String message = messageSource.getMessage("announcement.delete", null, LocaleContextHolder.getLocale());
        return message + " ID#" + announcement.getId();
    }
}
