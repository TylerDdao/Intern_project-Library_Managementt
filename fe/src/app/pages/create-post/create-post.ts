import { ChangeDetectorRef, Component, Inject, inject, PLATFORM_ID } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
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
import { backendUrl } from '../../../assets/constants';

@Component({
  selector: 'app-create-post',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, BookListComponent],
  templateUrl: './create-post.html',
  styleUrl: './create-post.css',
})
export class CreatePost {
  post!:Post
  postId!:number;
  bookCover: string = ""

  backendUrl = backendUrl;

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

  constructor(
    private route: ActivatedRoute,
    private langService: LanguageService,
    private postService: PostService,
    private bookService: BookService,
    private cdr: ChangeDetectorRef,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  postForm = new FormGroup({
    subject: new FormControl('', Validators.required),
    content: new FormControl('', Validators.required),
    book: new FormControl<number | null>(null, Validators.required),
  });
  
  handleUnselectBook(){
    this.chosenBook = null;
    this.cdr.markForCheck();
  }

  handleSelectBook(book:Book){
    console.log('handleSelectBook called', book);
    this.chosenBook = book;
    this.postForm.patchValue({ book: book.id });
    this.bookCover = this.chosenBook.title.replaceAll(' ', '-').toLowerCase();
    this.isOpenBookList = false;
    this.cdr.markForCheck();
  }

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
