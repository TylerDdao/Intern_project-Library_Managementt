import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ExportService } from '../../services/export-service/export-service';
import { isPlatformBrowser } from '@angular/common';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { Announcement } from '../../models/announcement';

@Component({
  selector: 'app-logs-management',
  imports: [NavbarComponent, TranslateModule, ReactiveFormsModule, AnnouncementComponent],
  templateUrl: './logs-management.html',
  styleUrl: './logs-management.css',
})
export class LogsManagement {
  constructor(
    private exportService: ExportService,
    @Inject(PLATFORM_ID) private platformId:Object,
    private announcementService: AnnouncementService
  ){}
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
  }


  today = new Date().toISOString().split('T')[0];
  isValid: { [key: string]: boolean } = {
    from: true,
    to: true,
  };
  logForm = new FormGroup({
    from: new FormControl<string>(this.today, Validators.required),
    to: new FormControl<string>(this.today, Validators.required)
  });
  
  onSubmit(){
    this.isValid['from'] = this.logForm.get('from')?.valid ?? false;
    this.isValid['to'] = this.logForm.get('to')?.valid ?? false;

    if (this.logForm.invalid) {
      return;
    }
    let { from, to } = this.logForm.value;
    if (!from || !to) {
      return;
    }
    if (from > to) {
      [from, to] = [to, from];
    }
    this.exportService.exportLogs(from, to)
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      this.logForm.get('from')?.valueChanges.subscribe(() => {
        this.isValid["from"] = true
      });
      this.logForm.get('to')?.valueChanges.subscribe(() => {
        this.isValid["to"] = true
      });
    }
  }
}
