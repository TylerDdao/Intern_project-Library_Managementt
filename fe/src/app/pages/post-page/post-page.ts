import { ChangeDetectorRef, Component, inject, Inject, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { formatTime } from '../../util/format-number';
import { Post } from '../../models/post';
import { LanguageService } from '../../services/language-service/language-service';
import { isPlatformBrowser } from '@angular/common';
import { PostService } from '../../services/post-service/post-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Book } from '../../models/book';
import { BookService } from '../../services/book-service/book-service';
import { BookListComponent } from '../../components/book-list-component/book-list-component';
import { Page } from '../../models/page';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { errorImage } from '../../../assets/constants';
import { NavigationService } from '../../services/navigation-service/navigation-service';

@Component({
  selector: 'app-post-page',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, BookListComponent],
  templateUrl: './post-page.html',
  styleUrl: './post-page.css',
})
export class PostPage {
  backendUrl = environment.apiUrl;
  post!:Post
  postId!:number;

  books!: Book[];
  booksPage: Page = {
    first: true,
    last: true, 
    number: 0,
    totalPages: 1
  }
  isOpenBookList: boolean = false;

  chosenBook!: Book;

  isLoading: boolean = false

  isBookValid: boolean = true;
  isSubjectValid: boolean = true;
  isContentValid: boolean = true;

  isPostSame:boolean = false

  constructor(
    private route: ActivatedRoute,
    private langService: LanguageService,
    private postService: PostService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService,
    private router:Router,
    private naviagtionService: NavigationService
  ){}

  postForm = new FormGroup({
    subject: new FormControl('', Validators.required),
    content: new FormControl('', Validators.required),
  });

  handleCancel(){
    this.router.navigate([this.naviagtionService.getPreviousUrl()])
  }

  onSubmit(): void {
    this.isBookValid = !!this.chosenBook;
    this.isSubjectValid = this.postForm.get('subject')?.valid ?? false;
    this.isContentValid = this.postForm.get('content')?.valid ?? false;
    if (!this.isBookValid || !this.isSubjectValid || !this.isContentValid) return;

    const { subject, content } = this.postForm.value;
    
    if(subject == this.post.subject && content == this.post.content){
      this.isPostSame = true
      return
    }

    if (!subject || !content || !this.chosenBook || !this.postId) return;

    this.isLoading = true;

    const request: Post = {
      id: this.postId,
      subject,
      content,
      book: this.chosenBook,
      likeCount: 0,
      createdAt: '',
      commentCount: 0,
      createdBy: '',
      liked: false,
      editable: true
    };

    this.postService.updatePost(request).subscribe({
      next: (data: any) => {
        if (data.code !== '200') {
          this.isLoading = false;
          return;
        }
        const message = this.translate.instant('postsManagement.Post-is-updated');
        alert(message)
        this.router.navigate(['my-posts'])
      },
      error: (err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  handleCloseBookList(){
    this.isOpenBookList = false;
    this.cdr.markForCheck();
  }

  handleDeletePost(){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message+"?")
    if(!option ) return
    if(this.post){
      this.isLoading = true
      this.postService.deletePost(this.post).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            const message = this.translate.instant("postsManagement.Post-is-deleted")
            alert(message)
            this.router.navigate(["/my-posts"])
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
        }
      })
    }
  }

  fetchBooks(page:Page = this.booksPage){
    this.isLoading = true
    this.bookService.getAllBooks(page.number).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.books = data.data.content
          this.booksPage = data.data
          this.isOpenBookList = true;
        }
        this.cdr.markForCheck()
        this.isLoading = false
      },
      error: (err:HttpErrorResponse) =>{
        errorNoti(err, this.translate)
        this.isLoading = false;
      }
    })
  }

  fetchPost(id: number){
    this.isLoading = true
    this.postService.getPostById(id).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.post = data.data
          this.postForm.patchValue({
            subject: this.post.subject,
            content: this.post.content,
          });
          this.chosenBook = this.post.book;
          this.isLoading = false
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading = false
      }
    })
  }


  ngOnInit(){
    this.postId = Number(this.route.snapshot.paramMap.get('post-id'));

    if(isPlatformBrowser(this.platformId)){
      this.fetchPost(this.postId);

      this.postForm.get('subject')?.valueChanges.subscribe(() => {
        this.isSubjectValid = true;
        this.isPostSame = false
      });
      this.postForm.get('content')?.valueChanges.subscribe(() => {
        this.isContentValid = true;
        this.isPostSame = false
      });
    }
  }

  get formattedPostedAt(): string{
    if(this.post){
      return formatTime(this.post.createdAt, this.langService.currentLang)
    }
    else{return ""}
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
