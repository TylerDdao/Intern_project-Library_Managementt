import { isPlatformBrowser } from '@angular/common';
import { Component, ElementRef, Inject, Input, PLATFORM_ID, ViewChild } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-post-card-component',
  imports: [TranslateModule],
  templateUrl: './post-card-component.html',
  styleUrl: './post-card-component.css',
})
export class PostCardComponent {
  @Input() bookName: string = ''; 
  @Input() author: string = '';
  @Input() genres: string[] = [];
  @Input() subject: string = '';
  @Input() content: string = '';
  @Input() likeCount: string = '';
  @Input() commentCount: string = '';

  @ViewChild('myPost') myChart!: ElementRef;
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}
}
