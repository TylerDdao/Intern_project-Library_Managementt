import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { ExportService } from '../../services/export-service/export-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement, AnnouncementType } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { allPages, webAnnouncements } from '../../../assets/constants';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { forkJoin } from 'rxjs';


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
    private announcementService: AnnouncementService,
    private translate: TranslateService
  ) 
  {}
  announcementsList: Announcement[] = []
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
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

  handleResetAnnouncementList(){
    this.announcementsList = this.announcements.map(a => ({
      ...a
    }));
    this.cdr.markForCheck()
  }

  handleDeleteAnnouncement(announcement: Announcement){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message+"?")
    if(!option) return
    this.announcementService.deleteAnnouncement(announcement).subscribe({
      next:(data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("announcementsManagement.Announcement-is-deleted")
          alert(message)
          this.announcementService.loadAnnouncements(true).subscribe({
            next: (announcements) => {
              this.announcements = announcements;
              this.announcementsList = this.announcements.map(a => ({
                ...a
              }));

              this.handleResetPreview();

              this.cdr.markForCheck();
            }
          });
        }
      }
    })
  }

  handleUpdateAnnouncement(announcements: Announcement[]) {
    const message = this.translate.instant("form.Confirm");
    const option = confirm(message + "?");

    if (!option) return;

    const requests = announcements.map(announcement =>
      this.announcementService.updateAnnouncement(announcement)
    );

    forkJoin(requests).subscribe({
      next: (responses: any[]) => {

        const allSuccessful = responses.every(
          response => response.code === "200"
        );

        if (!allSuccessful) {
          return;
        }

        const message = this.translate.instant("announcementsManagement.Announcements-is-updated");
        alert(message);
        this.announcementService.loadAnnouncements(true).subscribe({
          next: (announcements) => {
            this.announcements = announcements;
            this.announcementsList = this.announcements.map(a => ({
              ...a
            }));

            this.handleResetPreview();
            this.cdr.markForCheck();
          }
        });
      },

      error: (err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
      }
    });
  }

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
        active: active
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
    console.log("preview")
    const announcementType: AnnouncementType = type ?? 'info';
    this.previewAnnouncement = {
      id: 0,
      subjectVi: subject_vi,
      contentVi: content_vi,
      linkTextVi: linkText_vi ? linkText_vi : '',

      subjectEn: subject_en ? subject_en : '',
      contentEn: content_en ? content_en : '',
      linkTextEn: linkText_en ? linkText_en : '',

      subjectFr: subject_fr ? subject_fr : '',
      contentFr: content_fr ? content_fr : '',
      linkTextFr: linkText_fr ? linkText_fr : '',

      link: link ? link : '',
      type: announcementType,
      active: isActive ?? true,
      locations: locations ? locations : []
    }
    this.cdr.markForCheck()
  }

  pages = allPages;
  
  onSubmit(){
    this.isValid['subject'] = this.announcementForm.get('subject_vi')?.valid ?? false;
    this.isValid['content'] = this.announcementForm.get('content_vi')?.valid ?? false;
    const {subject_vi, subject_en, subject_fr, content_vi, content_en, content_fr, linkText_vi, linkText_en, linkText_fr, link, type, isActive, locations} = this.announcementForm.value

    if(!this.isValid['content'] || !this.isValid['subject']) return
    if(!subject_vi || !content_vi) return
    const announcementType: AnnouncementType = type ?? 'info';
    this.previewAnnouncement = {
      id: 0,
      subjectVi: subject_vi,
      contentVi: content_vi,
      linkTextVi: linkText_vi ? linkText_vi : '',

      subjectEn: subject_en ? subject_en : '',
      contentEn: content_en ? content_en : '',
      linkTextEn: linkText_en ? linkText_en : '',

      subjectFr: subject_fr ? subject_fr : '',
      contentFr: content_fr ? content_fr : '',
      linkTextFr: linkText_fr ? linkText_fr : '',

      link: link ? link : '',
      type: announcementType,
      active: isActive ?? true,
      locations: locations ? locations : []
    }

    this.announcementService.createAnnouncement(this.previewAnnouncement).subscribe({
      next:(data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("announcementsManagement.Announcement-is-posted")
          alert(message)
          this.announcementService.loadAnnouncements(true).subscribe({
            next: (announcements) => {
              this.announcements = announcements;
              this.announcementsList = this.announcements.map(a => ({
                ...a
              }));

              this.handleResetPreview();

              this.cdr.markForCheck();
            }
          });
        }
        this.handleResetPreview();
        this.cdr.markForCheck();
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
    

  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      this.announcementsList = this.announcementService.getAnnouncements();
      this.announcementForm.get('subject_vi')?.valueChanges.subscribe(() => {
        this.isValid["subject"] = true
      });
      this.announcementForm.get('content_vi')?.valueChanges.subscribe(() => {
        this.isValid["content"] = true
      });
    }
  }
}
