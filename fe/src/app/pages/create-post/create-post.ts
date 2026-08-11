import { ChangeDetectorRef, Component, Inject, inject, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BookListComponent } from '../../components/book-list-component/book-list-component';
import { isPlatformBrowser } from '@angular/common';
import { formatTime } from '../../util/format-number';
import { Post } from '../../models/post';
import { Book } from '../../models/book';
import { Page } from '../../models/page';
import { ActivatedRoute, Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { PostService } from '../../services/post-service/post-service';
import { BookService } from '../../services/book-service/book-service';
import { environment } from '../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { LoadingComponent } from "../../components/loading-component/loading-component";

@Component({
  selector: 'app-create-post',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, BookListComponent, LoadingComponent],
  templateUrl: './create-post.html',
  styleUrl: './create-post.css',
})
export class CreatePost {
  postId!:number;
  bookCover: string = ""

  backendUrl = environment.apiUrl;

  books!: Book[];
  booksPage: Page = {
    first: true,
    last: true, 
    number: 0,
    totalPages: 1
  }
  isOpenBookList: boolean = false;

  chosenBook: Book | null = null;

  username!:String

  isLoading: boolean = false

  isBookValid: boolean = false;
  isSubjectValid: boolean = false;
  isContentValid: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private langService: LanguageService,
    private postService: PostService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ){}

  postForm = new FormGroup({
    subject: new FormControl('', Validators.required),
    content: new FormControl('', Validators.required),
  });
  
  handleUnselectBook(){
    this.chosenBook = null;
    this.isBookValid = false;
    this.cdr.markForCheck();
  }

  handleSelectBook(book:Book){
    this.chosenBook = book;
    this.bookCover = this.chosenBook.title.replaceAll(' ', '-').toLowerCase();
    this.isOpenBookList = false;
    this.isBookValid = true;
    this.cdr.markForCheck();
  }

  handleCancel(){
    this.router.navigate(['/my-posts'])
  }

  onSubmit(){
    console.log("SUbmit")
    const { subject, content } = this.postForm.value;
    if (subject && content && this.chosenBook) {
      this.isLoading = true;
      const request: Post = {
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
      this.postService.createPost(request).subscribe({
        next: (data: any) => {
          if (data.code == "200") {
            const message = this.translate.instant("postsManagement.Post-is-created");
            alert(message);
            this.router.navigate(['/my-posts']); // or wherever makes sense after creating
          }
          this.isLoading = false;
        },
        error: (err: HttpErrorResponse) => {
          errorNoti(err, this.translate);
          this.isLoading = false;
        }
      });
    }
  }

  handleCloseBookList(){
    this.isOpenBookList = false;
    this.cdr.markForCheck();
  }

  fetchBooks(page:Page = this.booksPage){
    this.bookService.getAllBooks(page.number).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.books = data.data.content
          this.booksPage = data.data
          this.isOpenBookList = true;
          this.cdr.markForCheck();
        }
      },
      error: (err) =>{
        console.error(err)
      }
    })
  }


  ngOnInit(){
    this.postId = Number(this.route.snapshot.paramMap.get('post-id'));

    if(isPlatformBrowser(this.platformId)){
      const user = JSON.parse(sessionStorage.getItem('user') ?? '{}');
      this.username = user.username

      this.postForm.get('subject')?.valueChanges.subscribe(() => {
        this.isSubjectValid = this.postForm.get('subject')?.valid ?? false;
      });
      this.postForm.get('content')?.valueChanges.subscribe(() => {
        this.isContentValid = this.postForm.get('content')?.valid ?? false;
      });
    }
  }

  // get formattedPostedAt(): string{
  //   if(this.post){
  //     return formatTime(this.post.createdAt, this.langService.currentLang)
  //   }
  //   else{return ""}
  // }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }
}
