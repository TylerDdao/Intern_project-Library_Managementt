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
  @Input() user: string = 'Null';
  @Input() postedAt: string = 'Null';
  @Input() bookName: string = 'Null'; 
  @Input() author: string = 'Null';
  @Input() genres: string[] = [];
  @Input() subject: string = 'Null';
  @Input() content: string = 'Null';
  @Input() likeCount: string = 'Null';
  @Input() commentCount: string = 'Null';

  @ViewChild('myPost') myChart!: ElementRef;
  bookCover: string = '';
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

  ngOnInit(): void {
    this.bookCover = this.bookName.replaceAll(' ', '-').toLowerCase();
  }
}
