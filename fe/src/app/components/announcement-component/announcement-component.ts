import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-announcement-component',
  imports: [],
  templateUrl: './announcement-component.html',
  styleUrl: './announcement-component.css',
})
export class AnnouncementComponent {
  @Input({ required: true }) subject: string = "Subject";
  @Input({ required: true }) content: string = "Content";
  @Input({ required: false}) link!: string;
  @Input({ required: false }) type: "info" | "warning" | "error" = "info"
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private translate: TranslateService
  ){}

  close(){
    this.onClose.emit();
  }
}
