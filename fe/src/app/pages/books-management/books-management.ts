import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';
import { BookService } from '../../services/book-service/book-service';
import { isPlatformBrowser } from '@angular/common';
import { Genre } from '../../models/genre';
import { GenreService } from '../../services/genre-service/genre-service';
import { EMPTY, firstValueFrom } from 'rxjs';
import { expand, reduce } from 'rxjs/operators';
import { Page } from '../../models/page';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { BookListComponent } from '../../components/book-list-component/book-list-component';

@Component({
  selector: 'app-books-management',
  imports: [NavbarComponent, SortSideBarComponent, BookCardComponent, TranslateModule, PagesComponent, BookListComponent],
  templateUrl: './books-management.html',
  styleUrl: './books-management.css',
})
export class BooksManagement {
  constructor(
    public langService: LanguageService,
    private bookService: BookService,
    private genreService: GenreService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) 
  {}

  bookList!: Book[]
  bookListPage:Page = {
    totalPages: 1,
    number: 0,
    last: true,
    first: true
  }
  isOpenBookList:boolean = false;

  lastQuery: SideBarQuery | null = null;

  mostPostsBooks:Book[] = []
  mostBorrowedBooks: Book[] = []
  newlyArrivedBooks: Book[] = []
  genres: string[] = []

  searchBooks: Book[] = []
  isSearch:boolean = false;
  isBookFound:boolean = true;

  bookPages:Page = {
    totalPages: 1,
    number: 0,
    last: true,
    first: true
  }

  handleApply(query: SideBarQuery): void {
    if(query.isClear){
      this.isSearch = false;
      this.searchBooks = [];
      this.isBookFound = false;
      this.lastQuery = null ;
      return;
    }
    this.isSearch = true;
    this.searchBooks = [];
    this.isBookFound = true;
    this.lastQuery = query;
    this.bookService.getBooksByQuery(query).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          if(data.data.totalElements > 0){
            this.searchBooks = data.data.content;
            this.bookPages = data.data;
            this.cdr.markForCheck();
          }
          else{
            this.isBookFound = false;
            this.cdr.markForCheck();
          }
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  fetchLastQuery(page: Page){
    if(this.lastQuery){
      this.bookService.getBooksByQuery(this.lastQuery, page.number).subscribe({
        next: (data: any) => {
          if(data.code == "200"){
            if(data.data.totalElements > 0){
              this.searchBooks = data.data.content;
              this.bookPages = data.data;
              this.cdr.markForCheck();
            }
            else{
              this.isBookFound = false;
              this.cdr.markForCheck();
            }
          }
        },
        error: (err)=>{
          console.error(err)
        }
      })
    }
  }

  async fetchAllGenres(): Promise<void> {
    let isLast = false;
    let page = 0;
    const collectedGenres: Genre[] = [];

    try {
      while (!isLast) {
        const data: any = await firstValueFrom(this.genreService.getAllGenres(page, 50));        
        if (data && data.code === "200") {
          collectedGenres.push(...data.data.content);           
          isLast = data.data.last; 
          
          if (!isLast) {
            page++;
          }
        } else {
          break; 
        }
      }
      
      this.genres = collectedGenres.map(genre => genre.name);
      this.cdr.markForCheck();
    } catch (err) {
      console.error("Failed to load genres", err);
    }
  }
  
  fetchBooks(): void{
    this.bookService.getMostBorrowedBooks().subscribe({
      next: (data: any) =>{
        if(data.code ="200"){
          this.mostBorrowedBooks = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err)
      }
    })

    this.bookService.getMostPostsBooks().subscribe({
      next: (data: any) =>{
        if(data.code ="200"){
          this.mostPostsBooks = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err)
      }
    })

    this.bookService.getNewlyArrivedBooks().subscribe({
      next: (data: any) =>{
        if(data.code ="200"){
          this.newlyArrivedBooks = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  fetchBookList(page:Page = this.bookListPage){
    this.bookService.getAllBooks(page.number).subscribe({
      next:(data: any) => {
        if(data.code == "200"){
          this.bookList= data.data.content;
          this.isOpenBookList = true;
          this.cdr.markForCheck();
        }
      }
    })
  }

  handleCloseBookList(){
    this.isOpenBookList = false;
    this.cdr.markForCheck();
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllGenres();
      this.fetchBooks();
    }
  }
}
