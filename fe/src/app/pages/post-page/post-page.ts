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
  private router = inject(Router);

  chosenBook!: Book;

  constructor(
    private route: ActivatedRoute,
    private langService: LanguageService,
    private postService: PostService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ){}

  postForm = new FormGroup({
    subject: new FormControl('', Validators.required),
    content: new FormControl('', Validators.required),
    book: new FormControl<number | null>(null, Validators.required),
  });

  handleCancel(){
    this.router.navigate(['/my-posts'])
  }

  onSubmit(){
    console.log("Submit")
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

  fetchPost(id: number){
    this.postService.getPostById(id).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.post = data.data
          this.postForm.patchValue({
            subject: this.post.subject,
            content: this.post.content,
            book: this.post.book.id
          });
          this.chosenBook = this.post.book;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        console.error(err)
      }
    })
  }


  ngOnInit(){
    this.postId = Number(this.route.snapshot.paramMap.get('post-id'));

    if(isPlatformBrowser(this.platformId)){
      this.fetchPost(this.postId);
    }
  }

  get formattedPostedAt(): string{
    if(this.post){
      return formatTime(this.post.createdAt, this.langService.currentLang)
    }
    else{return ""}
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }
}
