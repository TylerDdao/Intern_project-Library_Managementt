package com.example.library_management.service.announcement;

import com.example.library_management.dto.request.announcement.AnnouncementRequest;
import com.example.library_management.dto.response.announcement.AnnouncementResponse;
import com.example.library_management.model.Announcement;
import com.example.library_management.model.AnnouncementType;
import com.example.library_management.repository.AnnouncementRepository;
import com.example.library_management.util.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class CreateAnnouncementService {
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private AuditLogger logger;
    @Autowired
    private AnnouncementRepository announcementRepository;

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request){
        Announcement announcement = new Announcement();

        announcement.setType(request.getType() != null ? request.getType() : AnnouncementType.INFO);

        announcement.setSubjectVi(request.getSubjectVi());
        announcement.setContentVi(request.getContentVi());

        announcement.setSubjectEn(request.getSubjectEn());
        announcement.setContentEn(request.getContentEn());

        announcement.setSubjectFr(request.getSubjectFr());
        announcement.setContentFr(request.getContentFr());

        announcement.setLink(request.getLink());
        announcement.setLinkTextVi(request.getLinkTextVi());
        announcement.setLinkTextEn(request.getLinkTextEn());
        announcement.setLinkTextFr(request.getLinkTextFr());

        announcement.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        announcement.setLocations(request.getLocations());
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        logger.log("Created announcement ID#{}", savedAnnouncement.getId());
        return new AnnouncementResponse(savedAnnouncement);

    }
}
