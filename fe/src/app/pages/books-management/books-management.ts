import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { Book } from '../../models/book';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { BookService } from '../../services/book-service/book-service';
import { isPlatformBrowser } from '@angular/common';
import { Genre } from '../../models/genre';
import { GenreService } from '../../services/genre-service/genre-service';
import { EMPTY, firstValueFrom } from 'rxjs';
import { expand, reduce } from 'rxjs/operators';
import { Page } from '../../models/page';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { BookListComponent } from '../../components/book-list-component/book-list-component';
import { NewBookForm } from '../../forms/new-book-form/new-book-form';
import { GenresManagementForm } from '../../forms/genres-management-form/genres-management-form';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { ExportBooksForm } from "../../forms/export/export-books-form/export-books-form";
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement } from '../../models/announcement';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';

@Component({
  selector: 'app-books-management',
  imports: [GenresManagementForm, NavbarComponent, SortSideBarComponent, BookCardComponent, TranslateModule, PagesComponent, BookListComponent, NewBookForm, ExportBooksForm, LoadingComponent, AnnouncementComponent],
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
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    private announcementService: AnnouncementService
  ) 
  {}
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
  }
    

  isCreateNewBook:boolean = false;
  isExportBook:boolean =false;

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

  bookPages:Page = {
    totalPages: 1,
    number: 0,
    last: true,
    first: true
  }

  isOpenGenreManagement: boolean = false;

  isLoading:boolean = true
  pendingRequests:number =0;

  private startLoading() {
    this.pendingRequests++;
    this.isLoading = true;
  }

  private finishLoading() {
    this.pendingRequests--;
    if (this.pendingRequests <= 0) {
      this.pendingRequests = 0;
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }

  handleCloseExportBook(){
    this.isExportBook = false;
  }

  handleChangeGenre(genres: Genre[]){
    this.isOpenGenreManagement = false;
    genres.forEach(genre=>{
      this.genreService.deleteGenre(genre).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            this.fetchBooks();
            this.cdr.markForCheck()
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
        }
      })
    })
  }

  handleCloseGenreManagement(){
    this.isOpenGenreManagement = false
    this.cdr.markForCheck();
  }

  handleCloseCreateBookForm(){
    this.isCreateNewBook = false;
    this.cdr.markForCheck();
  }

  handleApply(query: SideBarQuery): void {
    if(query.isClear){
      this.isSearch = false;
      this.searchBooks = [];
      this.lastQuery = null ;
      return;
    }
    this.startLoading()
    this.isSearch = true;
    this.searchBooks = [];
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
            this.cdr.markForCheck();
          }
          this.finishLoading()
        }
      },
      error: (err:HttpErrorResponse) => {
        errorNoti(err, this.translate)
        this.finishLoading()
      }
    })
  }

  fetchLastQuery(page: Page){
    this.startLoading()
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
              this.cdr.markForCheck();
            }
            this.finishLoading()
          }
        },
        error: (err)=>{
          errorNoti(err, this.translate)
          this.finishLoading()
        }
      })
    }
  }

  async fetchAllGenres(): Promise<void> {
    this.startLoading()
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
      this.finishLoading()
      this.cdr.markForCheck();
    } catch (err) {
      console.error("Failed to load genres", err);
      this.finishLoading()
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
      error: (err:HttpErrorResponse) => {
        errorNoti(err, this.translate)
      }
    })

    this.bookService.getMostPostsBooks().subscribe({
      next: (data: any) =>{
        if(data.code ="200"){
          this.mostPostsBooks = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse) => {
        errorNoti(err,this.translate)
      }
    })

    this.bookService.getNewlyArrivedBooks().subscribe({
      next: (data: any) =>{
        if(data.code ="200"){
          this.newlyArrivedBooks = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse) => {
        errorNoti(err,this.translate)
      }
    })
  }

  handleCloseBookList(){
    this.isOpenBookList = false;
    this.cdr.markForCheck();
  }

  handleAddBook(){
    this.fetchBooks()
    this.cdr.markForCheck()
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      this.fetchAllGenres();
      this.fetchBooks();
    }
  }
}
