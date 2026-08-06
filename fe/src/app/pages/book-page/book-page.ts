import { ChangeDetectorRef, Component, Inject, NgZone, OnChanges, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Book } from '../../models/book';
import { BookService } from '../../services/book-service/book-service';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { PostService } from '../../services/post-service/post-service';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { ChangeDetectionStrategy } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { Borrow } from '../../models/borrow';
import { formatTime } from '../../util/format-number';
import { LanguageService } from '../../services/language-service/language-service';
import { User } from '../../models/user';
import { getUser } from '../../util/session-storage';
import { environment } from '../../../environments/environment';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Genre } from '../../models/genre';
import { GenreService } from '../../services/genre-service/genre-service';
import { Page } from '../../models/page';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';


@Component({
  selector: 'app-book-page',
  imports: [NavbarComponent, PostCardComponent, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './book-page.html',
  styleUrl: './book-page.css',
  changeDetection: ChangeDetectionStrategy.Default
})
export class BookPage{
  editable: boolean = false;
  bookId!: number;
  book!: Book;
  bookCover = '';

  baseUrl = environment.apiUrl

  isLoadingPosts: boolean = true;
  posts: Post[] = [];

  isBorrowing: boolean = true;
  
  query:string = ''
  searchGenres:Genre[] = []
  searchGenresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1,
    totalElements: 0,
    numberOfElements: 0
  }

  borrow!: Borrow
  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private postService: PostService,
    private borrowService: BorrowService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    private langService: LanguageService,
    private translate:TranslateService,
    private genreService: GenreService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  editBookForm = new FormGroup({
    id: new FormControl<number>(0),
    title: new FormControl('', Validators.required),
    author: new FormControl('', Validators.required),
    genres: new FormControl<Genre[]>([]),
    copies: new FormControl<number>(0, Validators.required),
  });

  handleAddGenre(genre: Genre) {
    const genres = this.editBookForm.controls.genres.value ?? [];

    if (genres.some(g => g.id === genre.id)) {
      return;
    }

    this.editBookForm.patchValue({
      genres: [...genres, genre]
    });

    this.cdr.markForCheck();
  }

  handleRemoveGenre(genre: Genre) {
    const genres = this.editBookForm.value.genres ?? [];

    this.editBookForm.patchValue({
      genres: genres.filter(g => g.id !== genre.id)
    });

    this.book.genres = genres.filter(g => g.id !== genre.id)

    this.cdr.markForCheck()
  }

  onSubmit(){
    const {id, title, author, genres, copies} = this.editBookForm.value
    if(id && title && author &&  copies){
      
      let newBook:Book = {
        id: id,
        title: title,
        author: author,
        genres: genres ? genres : [],
        copies: copies
      }
      this.bookService.updateBook(newBook).subscribe({
        next:(data:any)=>{
          if(data.code == "200"){
            const message = this.translate.instant("booksManagement.Book-is-updated")
            alert(message)
            this.bookId = Number(this.route.snapshot.paramMap.get('book-id'));
            this.fetchBook(this.bookId);
            this.fetchPosts(this.bookId)
            this.query = ''
            this.searchGenres = []
            this.cdr.markForCheck()
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
        }
      })
    }
  }

  handleBorrow(){
    const message = this.translate.instant('bookPage.Confirm-borrow');
    const confirmed = confirm(message);
    if(this.book && confirmed){
      this.borrowService.createBorrow(this.book).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            alert("Borrow created")
            alert("Borrow dues on: " + new Date(data.data.dueDate).toDateString())
            this.book.borrowed = true; 
            this.borrow = data.data;
            this.cdr.markForCheck();
          }
        },
        error: (err) => {
          alert(err.message)
        }
      })
    }
  }

  fetchGenres(){
    this.genreService.getGenresByName(this.query ,this.searchGenresPage.number, 10).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.searchGenresPage = data.data
          this.searchGenres = data.data.content
          this.cdr.markForCheck()
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate);
      }
    })
  }

  handleLoadMoreGenre(){
    this.searchGenresPage.number++;
    this.genreService.getGenresByName(this.query,this.searchGenresPage.number, 10).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.searchGenres.push(...data)
          this.searchGenres = data.data.content
          this.cdr.markForCheck()
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate);
      }
    })
  }

  fetchPosts(id: number):void{
    this.postService.getPostsByBookId(id).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.posts = data.data.content;
          this.isLoadingPosts = false;
          this.cdr.markForCheck(); 
        }
      },
      error: (err) => { 
        console.error(err);
      }
    })
  }

  fetchBorrow(id: number){
    this.borrowService.getMyBorrows(true, id).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.borrow = data.data.content[0];
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchBook(id: number):void{
    this.bookService.getBookById(id).subscribe({
      next: (data: any) => {
        if (data.code == "200") {
          this.ngZone.run(() => {
            this.book = data.data;
            this.bookCover = this.book.title.replaceAll(' ', '-').toLowerCase();
            console.log(this.book);
            if(this.book.borrowed){
              this.fetchBorrow(this.book.id);
            }
            if(this.editable){
              this.editBookForm.patchValue({
                id: this.book.id,
                title: this.book.title,
                author: this.book.author,
                copies: this.book.copies,
                genres: this.book.genres
              })
            }
            this.cdr.markForCheck();
          });
        }
      },
      error:(err)=>{
        console.error(err)
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.bookId = Number(this.route.snapshot.paramMap.get('book-id'));
      this.fetchBook(this.bookId);
      this.fetchPosts(this.bookId);
      const sessionUser = getUser();
      if(sessionUser){
        if (sessionUser.role?.features.some(feature => feature.name === "UPDATE_BOOK") || sessionUser.role?.name == "ROLE_ROOT") {
            this.editable = true;
        }
      }
    }
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }

  get formattedDueDate(): string{
    return formatTime(this.borrow.dueDate, this.langService.currentLang)
  }
}
