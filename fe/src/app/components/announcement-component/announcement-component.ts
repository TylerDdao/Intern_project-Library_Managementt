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
  @Input({ required: false }) preview: boolean = false;
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private translate: TranslateService,
    protected langService: LanguageService
  ){}

  get isVisible(): boolean {
    if(this.preview){
      return true
    }
    if (!this.announcement) {
      return false;
    }

    // No specific locations = show everywhere
    if (!this.announcement.locations?.length) {
      return true;
    }

    // Has specific locations = must match current location
    console.log(!!this.location && this.announcement.locations.includes(this.location))
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
          this.announcement.subjectVi &&
          this.announcement.contentVi
        );

      case 'en':
        return !!(
          this.announcement.subjectEn &&
          this.announcement.contentEn
        );

      case 'fr':
        return !!(
          this.announcement.subjectFr &&
          this.announcement.contentFr
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
      return this.announcement.subjectEn || this.announcement.subjectVi;
    }

    if (this.langService.currentLang === 'fr') {
      return this.announcement.subjectFr || this.announcement.subjectVi;
    }

    return this.announcement.subjectVi;
  }

  getAnnouncementContent(): string {
    if (!this.announcement) {
      return '';
    }

    if (this.langService.currentLang === 'en') {
      return this.announcement.contentEn || this.announcement.contentVi;
    }

    if (this.langService.currentLang === 'fr') {
      return this.announcement.contentFr || this.announcement.contentVi;
    }

    return this.announcement.contentVi;
  }

  private firstNonEmpty(...values: (string | undefined)[]): string {
    for (const value of values) {
      if (value && value.trim().length > 0) {
        return value;
      }
    }
    return '';
  }

  getAnnouncementLinkText(): string {
    if (!this.announcement) {
      return '';
    }

    const textByLang: Record<string, string | undefined> = {
      en: this.announcement.linkTextEn,
      fr: this.announcement.linkTextFr,
      vi: this.announcement.linkTextVi,
    };

    return this.firstNonEmpty(
      textByLang[this.langService.currentLang],
      this.announcement.linkTextVi,
      this.announcement.link
    );
  }
}
