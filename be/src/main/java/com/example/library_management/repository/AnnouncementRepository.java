package com.example.library_management.repository;

import com.example.library_management.model.Announcement;
import com.example.library_management.model.AnnouncementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
//    Optional<Announcement>findByIdAndIsActive(boolean isActive);
//    Page<Announcement>findByIsActive(boolean isActive, Pageable pageable);
    Page<Announcement> findByTypeAndIsActive(AnnouncementType type, boolean isActive, Pageable pageable);

}
