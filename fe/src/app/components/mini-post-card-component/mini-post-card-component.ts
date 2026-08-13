import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { formatNumber, formatTime } from '../../util/format-number';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';
import { PostService } from '../../services/post-service/post-service';
import { errorImage } from '../../../assets/constants';
import { environment } from '../../../environments/environment';
import { CommentBoxComponent } from "../comment-box-component/comment-box-component";
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';

@Component({
  selector: 'app-mini-post-card-component',
  imports: [TranslateModule],
  templateUrl: './mini-post-card-component.html',
  styleUrl: './mini-post-card-component.css',
})
export class MiniPostCardComponent {
  @Input({required: true}) post!: Post
  @Output() onLikeToggled = new EventEmitter<Post>();

  @ViewChild('myPost') myChart!: ElementRef;
  bookCover: string = '';
  constructor(
    public langService: LanguageService,
    private cdr: ChangeDetectorRef,
    private postService: PostService,
    private translate: TranslateService
  ) {}

  backendUrl = environment.apiUrl;
  isOpenComment:boolean = false;
  comments!: Comment[]

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
        error: (err:HttpErrorResponse) =>{
          errorNoti(err, this.translate)
        } 
      });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post'] && this.post) {
        this.bookCover = this.post.book.title.replaceAll(' ', '-').toLowerCase();
    }
  }

  handleCloseComment(){
    this.isOpenComment = false
    this.cdr.markForCheck();
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
