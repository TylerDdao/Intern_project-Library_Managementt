import { Component, ElementRef, Input, SimpleChanges, ViewChild, OnChanges, OnInit, OnDestroy, Output, EventEmitter, ChangeDetectorRef, inject } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { formatNumber, formatTime } from '../../util/format-number';
import { LanguageService } from '../../services/language-service/language-service';
import { Subscription } from 'rxjs';
import { PostService } from '../../services/post-service/post-service';
import { CommentBoxComponent } from '../comment-box-component/comment-box-component';
import { CommentService } from '../../services/comment-service/comment-service';
import { Comment } from '../../models/comment';
import { Router } from '@angular/router';
import { backendUrl, errorImage } from '../../../assets/constants';

@Component({
  selector: 'app-post-card-component',
  imports: [TranslateModule, CommentBoxComponent],
  templateUrl: './post-card-component.html',
  styleUrl: './post-card-component.css',
})
export class PostCardComponent implements OnChanges, OnInit, OnDestroy {
  @Input({ required: true }) post!: Post;
  @Input() accessible:boolean = true;
  @Output() onLikeToggled = new EventEmitter<Post>();

  bookCover: string = '';
  isOpenComment:boolean = false;
  comments!: Comment[]
  private router = inject(Router);

  
  private langSubscription!: Subscription;

  @ViewChild('myPost') myChart!: ElementRef; // Note: You aren't referencing #myPost in the html yet!

  constructor(
    public langService: LanguageService, 
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private postService: PostService,
    private commentService: CommentService
  ) {}
  backendUrl = backendUrl;

  handleEditPost(){
    if(this.post){
      this.router.navigate([`/my-posts/${this.post.id}`])
    }
  }

  fetchComments(){
    this.commentService.getComments(this.post.id, 0, 10).subscribe({
      next: (data: any) => {
        if(data.code == "200") {
          this.comments = data.data.content
          this.cdr.markForCheck();
        }
      }
    })
  }

  toggleLike() {
    this.postService.toggleLike(this.post.id).subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.post = {
                    ...this.post,
                    liked: !this.post.liked,
                    likeCount: this.post.liked ? this.post.likeCount - 1 : this.post.likeCount + 1
                };
                this.onLikeToggled.emit(this.post);
                this.cdr.markForCheck();
            }
        },
        error: (err) => console.error(err)
    });
  }

  toggleCommentBox(){
    this.fetchComments();
    this.isOpenComment = !this.isOpenComment;
    this.cdr.markForCheck();
  }

  handleOpenComment(){
    this.fetchComments();
    this.isOpenComment = true;
    this.cdr.markForCheck();
  }

  handleCloseComment(){
    this.isOpenComment = false
    this.cdr.markForCheck();
  }

  ngOnInit(): void {
    // // 1. Subscribe ONLY ONCE during component creation
    this.langSubscription = this.translate.onLangChange.subscribe(() => {
      formatTime(this.post.createdAt, this.langService.currentLang)
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post'] && this.post) {
      this.bookCover = this.post.book.title.replaceAll(' ', '-').toLowerCase();
      formatTime(this.post.createdAt, this.langService.currentLang)
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

  get formattedPostedAt(): string{
    return formatTime(this.post.createdAt, this.langService.currentLang)
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = errorImage;
  }
}