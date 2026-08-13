import { Injectable } from '@angular/core';
import { Announcement } from '../../models/announcement';
import { webAnnouncements } from '../../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  announcements: Announcement[] = webAnnouncements;

  closeAnnouncement(id: number): void {
    const announcement = this.announcements.find(a => a.id === id);

    if (announcement) {
      announcement.isActive = false;
    }
  }

  getAnnouncements(): Announcement[] {
    return this.announcements;
  }
}