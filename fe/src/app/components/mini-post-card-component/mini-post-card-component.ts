import { TranslateModule } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { formatNumber, formatTime } from '../../util/format-number';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';
import { PostService } from '../../services/post-service/post-service';
import { errorImage } from '../../../assets/constants';
import { environment } from '../../../environments/environment';
import { CommentBoxComponent } from "../comment-box-component/comment-box-component";

@Component({
  selector: 'app-mini-post-card-component',
  imports: [TranslateModule, CommentBoxComponent],
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
    private postService: PostService
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
        error: (err) => console.error(err)
      });
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['post'] && this.post) {
        this.bookCover = this.post.book.title.replaceAll(' ', '-').toLowerCase();
        console.log(this.post)
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

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = errorImage;
  }
}
