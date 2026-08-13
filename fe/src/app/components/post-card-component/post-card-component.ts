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
import { errorImage } from '../../../assets/constants';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';

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
  backendUrl = environment.apiUrl;

  handleChange(commentCount:number){
    if(this.post.commentCount){
      this.post.commentCount += commentCount;
    }
    this.cdr.markForCheck
    console.log(this.post.commentCount)
  }

  handleEditPost(){
    if(this.post){
      this.router.navigate([`/my-posts/${this.post.id}`])
    }
  }

  fetchPost(){
    if(this.post && this.post.id){
      this.postService.getPostById(this.post.id).subscribe({
        next:(data:any)=>{
          if(data.code == "200"){
            this.post = data.data.content
            this.cdr.markForCheck()
          }
        },
        error: (err:HttpErrorResponse) => {
          errorNoti(err, this.translate)
        }
      })
    }
  }

  fetchComments(){
    if(this.post && this.post.id){
      this.commentService.getComments(this.post.id, 0, 10).subscribe({
        next: (data: any) => {
          if(data.code == "200") {
            this.comments = data.data.content
            this.cdr.markForCheck();
          }
        },
        error: (err:HttpErrorResponse) => {
          errorNoti(err, this.translate)
        }
      })
    }
  }

  toggleLike() {
    if(this.post && this.post.id){
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
          error: (err:HttpErrorResponse) => {
            errorNoti(err, this.translate)
          }
      });
    }
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

  getWebpCover(coverUrl: string | null | undefined): string {
    if (!coverUrl) {
      return 'default.webp';
    }

    return coverUrl.replace(/\.(jpg|jpeg|png)$/i, '.webp');
  }

  onImageError(event: Event): void {
    const image = event.target as HTMLImageElement;

    image.onerror = null;
    image.src = errorImage;
  }
}