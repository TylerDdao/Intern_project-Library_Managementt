import { TranslateModule } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { formatNumber, formatTime } from '../../util/format-number';
import { Component, ElementRef, Input, SimpleChanges, ViewChild } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-mini-post-card-component',
  imports: [TranslateModule],
  templateUrl: './mini-post-card-component.html',
  styleUrl: './mini-post-card-component.css',
})
export class MiniPostCardComponent {
  @Input({required: true}) post!: Post

  @ViewChild('myPost') myChart!: ElementRef;
  bookCover: string = '';
  constructor(public langService: LanguageService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post'] && this.post) {
        this.bookCover = this.post.book.title.replaceAll(' ', '-').toLowerCase();
        console.log(this.post)
    }
  }

  get formattedLikeCount(): string {
    return formatNumber(Number(this.post.likeCount));
  }

  get formattedCommentCount(): string {
      return formatNumber(Number(this.post.commentCount));
  }

  get formattedPostedAt(): string{
    return formatTime(this.post.createdAt, this.langService.currentLang)
  }
}
