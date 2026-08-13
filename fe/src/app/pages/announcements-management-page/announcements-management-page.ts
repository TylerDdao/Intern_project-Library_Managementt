import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { ExportService } from '../../services/export-service/export-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement, AnnouncementType } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { allPages, webAnnouncements } from '../../../assets/constants';


@Component({
  selector: 'app-announcements-management-page',
  imports: [ReactiveFormsModule, NavbarComponent, TranslateModule, AnnouncementComponent],
  templateUrl: './announcements-management-page.html',
  styleUrl: './announcements-management-page.css',
})
export class AnnouncementsManagementPage {
  constructor(
    private exportService: ExportService,
    @Inject(PLATFORM_ID) private platformId:Object,
    private cdr: ChangeDetectorRef,
    private announcementService: AnnouncementService
  ) 
  {}
  announcementsList: Announcement[] = webAnnouncements.map(a => ({
    ...a
  }));
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
  }

  isValid: { [key: string]: boolean } = {
    subject: true,
    content: true,    
  };

  previewAnnouncement: Announcement | null = null


  announcementForm = new FormGroup({
    subject_vi: new FormControl<string>("", Validators.required),
    content_vi: new FormControl<string>("", Validators.required),
    linkText_vi: new FormControl<string>(""),

    subject_en: new FormControl<string>(""),
    content_en: new FormControl<string>(""),
    linkText_en: new FormControl<string>(""),

    subject_fr: new FormControl<string>(""),
    content_fr: new FormControl<string>(""),
    linkText_fr: new FormControl<string>(""),

    link: new FormControl<string>(""),
    type: new FormControl<AnnouncementType>("info", Validators.required),
    isActive: new FormControl<boolean>(true, Validators.required),
    locations: new FormControl<string[]>([])
  });

  selectedAnnouncementType: AnnouncementType = 'info';
  announcementActivity: boolean = true;
  selectedLocation: string[] =[]

  toggleLocation(location: string) {
    if (this.selectedLocation.includes(location)) {
      this.selectedLocation = this.selectedLocation.filter(
        l => l !== location
      );
    } else {
      this.selectedLocation.push(location);
    }

    this.announcementForm.patchValue({
      locations: this.selectedLocation
    })
  }
  
  setAnnouncementType(type: AnnouncementType): void {
    this.selectedAnnouncementType = type;

    this.announcementForm.patchValue({
      type: type
    });

    if (this.previewAnnouncement) {
      this.previewAnnouncement = {
        ...this.previewAnnouncement,
        type: type
      };
    }

    this.cdr.markForCheck();
  }

  setActive(active: boolean): void {
    this.announcementActivity = active;

    this.announcementForm.patchValue({
      isActive: active
    });

    if (this.previewAnnouncement) {
      this.previewAnnouncement = {
        ...this.previewAnnouncement,
        isActive: active
      };
    }

    this.cdr.markForCheck();
  }

  handleResetPreview(){
    this.previewAnnouncement = null
    this.announcementForm.patchValue({
      subject_vi: '',
      content_vi: '',
      linkText_vi: '',

      subject_en: '',
      content_en: '',
      linkText_en: '',

      subject_fr: '',
      content_fr: '',
      linkText_fr:'',

      link: '',
      type: 'info',
      isActive: true
    })
    this.selectedAnnouncementType = 'info'
    this.announcementActivity = true
    this.cdr.markForCheck();
  }

  handlePreview(){
    const {subject_vi, content_vi, linkText_vi, subject_en, content_en, linkText_en, subject_fr, content_fr, linkText_fr, link, type, isActive, locations} = this.announcementForm.value
    if(!subject_vi || !content_vi){
      this.isValid["subject"] = false
      this.isValid["content"] = false
      return
    }
    const announcementType: AnnouncementType = type ?? 'info';
    this.previewAnnouncement = {
      id: 0,
      subject_vi: subject_vi,
      content_vi: content_vi,
      linkText_vi: linkText_vi ? linkText_vi : '',

      subject_en: subject_en ? subject_en : '',
      content_en: content_en ? content_en : '',
      linkText_en: linkText_en ? linkText_en : '',

      subject_fr: subject_fr ? subject_fr : '',
      content_fr: content_fr ? content_fr : '',
      linkText_fr: linkText_fr ? linkText_fr : '',

      link: link ? link : '',
      type: announcementType,
      isActive: isActive ?? true,
      locations: locations ? locations : []
    }
    this.cdr.markForCheck()
  }

  pages = allPages;
  
  onSubmit(){
    this.isValid['subject'] = this.announcementForm.get('subject_vi')?.valid ?? false;
    this.isValid['content'] = this.announcementForm.get('content_vi')?.valid ?? false;

    // if (this.logForm.invalid) {
    //   return;
    // }
    // let { from, to } = this.logForm.value;
    // if (!from || !to) {
    //   return;
    // }
    // if (from > to) {
    //   [from, to] = [to, from];
    // }
    // this.exportService.exportLogs(from, to)
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      this.announcementForm.get('subject_vi')?.valueChanges.subscribe(() => {
        this.isValid["subject"] = true
      });
      this.announcementForm.get('content_vi')?.valueChanges.subscribe(() => {
        this.isValid["content"] = true
      });
    }
  }
}
