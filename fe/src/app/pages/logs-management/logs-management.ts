import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ExportService } from '../../services/export-service/export-service';
import { isPlatformBrowser } from '@angular/common';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { Announcement } from '../../models/announcement';
import { LoadingComponent } from "../../components/loading-component/loading-component";

@Component({
  selector: 'app-logs-management',
  imports: [NavbarComponent, TranslateModule, ReactiveFormsModule, AnnouncementComponent, LoadingComponent],
  templateUrl: './logs-management.html',
  styleUrl: './logs-management.css',
})
export class LogsManagement {
  constructor(
    private exportService: ExportService,
    @Inject(PLATFORM_ID) private platformId:Object,
    private announcementService: AnnouncementService,
    private cdr: ChangeDetectorRef
  ){}
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
  }

  isProcessing:boolean = false;


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
    this.isProcessing = true;
    if (from > to) {
      [from, to] = [to, from];
    }
    this.exportService.exportLogs(from, to)
    this.isProcessing = false;
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
