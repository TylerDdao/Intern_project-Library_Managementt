import { Component, ElementRef, Input, SimpleChanges, ViewChild, OnChanges, OnInit, OnDestroy } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { formatNumber } from '../../util/format-number';
import { LanguageService } from '../../services/language-service/language-service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-post-card-component',
  imports: [TranslateModule],
  templateUrl: './post-card-component.html',
  styleUrl: './post-card-component.css',
})
export class PostCardComponent implements OnChanges, OnInit, OnDestroy {
  @Input({ required: true }) post!: Post;

  formattedCreatedAt: string = '';
  bookCover: string = '';
  
  private langSubscription!: Subscription;

  @ViewChild('myPost') myChart!: ElementRef; // Note: You aren't referencing #myPost in the html yet!

  constructor(
    public langService: LanguageService, 
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    // // 1. Subscribe ONLY ONCE during component creation
    // this.langSubscription = this.translate.onLangChange.subscribe(() => {
    //   this.formatDate();
    // });
  }
  formatDate() {
    throw new Error('Method not implemented.');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post'] && this.post) {
      this.bookCover = this.post.book.title.replaceAll(' ', '-').toLowerCase();
      this.formatDate();
    }
  }

  ngOnDestroy(): void {
    // 2. Prevent memory leaks by cleanly unsubscribing when the component vanishes
    if (this.langSubscription) {
      this.langSubscription.unsubscribe();
    }
  }

  get formattedLikeCount(): string {
    return formatNumber(Number(this.post.likeCount));
  }

  get formattedCommentCount(): string {
    return formatNumber(Number(this.post.commentCount));
  }

  toggleLike(){
    this.post.liked = !this.post.liked;
  }
}