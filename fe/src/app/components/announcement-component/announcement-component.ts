import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Announcement } from '../../models/announcement';
import { LanguageService } from '../../services/language-service/language-service';


@Component({
  selector: 'app-announcement-component',
  imports: [TranslateModule],
  templateUrl: './announcement-component.html',
  styleUrl: './announcement-component.css',
})
export class AnnouncementComponent {
  @Input({ required: false }) announcement: Announcement | null = null
  @Input({ required: false}) showActiveIndicator: boolean = false
  @Input({ required: false }) location: string | null = null
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private translate: TranslateService,
    protected langService: LanguageService
  ){}

  get isVisible(): boolean {
    if (!this.announcement) {
      return false;
    }

    // No specific locations = show everywhere
    if (!this.announcement.locations?.length) {
      return true;
    }

    // Has specific locations = must match current location
    return !!this.location && this.announcement.locations.includes(this.location);
  }

  close(){
    this.onClose.emit();
  }

  isLangAvailable(lang: string): boolean {
    if (!this.announcement) {
      return false;
    }

    switch (lang) {
      case 'vi':
        return !!(
          this.announcement.subject_vi &&
          this.announcement.content_vi
        );

      case 'en':
        return !!(
          this.announcement.subject_en &&
          this.announcement.content_en
        );

      case 'fr':
        return !!(
          this.announcement.subject_fr &&
          this.announcement.content_fr
        );

      default:
        return false;
    }
  }

  getAnnouncementSubject(): string {
    if (!this.announcement) {
      return '';
    }

    if (this.langService.currentLang === 'en') {
      return this.announcement.subject_en || this.announcement.subject_vi;
    }

    if (this.langService.currentLang === 'fr') {
      return this.announcement.subject_fr || this.announcement.subject_vi;
    }

    return this.announcement.subject_vi;
  }

  getAnnouncementContent(): string {
    if (!this.announcement) {
      return '';
    }

    if (this.langService.currentLang === 'en') {
      return this.announcement.content_en || this.announcement.content_vi;
    }

    if (this.langService.currentLang === 'fr') {
      return this.announcement.content_fr || this.announcement.content_vi;
    }

    return this.announcement.content_vi;
  }

  getAnnouncementLinkText(): string {
    if (!this.announcement) {
      return '';
    }

    if (this.langService.currentLang === 'en') {
      return (
        this.announcement.linkText_en ??
        this.announcement.linkText_vi ??
        this.announcement.link ??
        ''
      );
    }

    if (this.langService.currentLang === 'fr') {
      return (
        this.announcement.linkText_fr ??
        this.announcement.linkText_vi ??
        this.announcement.link ??
        ''
      );
    }

    return (
      this.announcement.linkText_vi ??
      this.announcement.link ??
      ''
    );
  }
}
