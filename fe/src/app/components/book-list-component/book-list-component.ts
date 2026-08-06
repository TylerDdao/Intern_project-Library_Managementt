import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { Page } from '../../models/page';
import { Book } from '../../models/book';
import { BookCardComponent } from '../book-card-component/book-card-component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { PagesComponent } from '../pages-component/pages-component';
import { BookService } from '../../services/book-service/book-service';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../loading-component/loading-component';
import { SideBarQuery, SortSideBarComponent } from '../sort-side-bar-component/sort-side-bar-component';
import { Genre } from '../../models/genre';
import { firstValueFrom } from 'rxjs';
import { GenreService } from '../../services/genre-service/genre-service';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-book-list-component',
  imports: [BookCardComponent, TranslateModule, PagesComponent, LoadingComponent, FormsModule],
  templateUrl: './book-list-component.html',
  styleUrl: './book-list-component.css',
})
export class BookListComponent{
  @Input() selectable: boolean = false;
  @Input() deletable: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<Page>();
  @Output() onSelect = new EventEmitter<Book>();

  constructor(
    private cdr: ChangeDetectorRef,
    private bookService: BookService,
    private genreService: GenreService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ){}

  bookList: Book[] = [];
  bookListPage: Page = {
    number: 0,
    totalPages: 1,
    last: true,
    first: true
  }
  isLoadingBookList: boolean = true

  totalBooks:number = 0;

  resultPage: Page = {
    number: 0,
    totalPages: 1,
    last: true,
    first: true
  }
  query:string = ""
  searchBooks: Book[] = []
  isSearch:boolean | null = null;
  isBookFound:boolean = true;

  handleClear(){
    this.searchBooks = [];
    this.isSearch = null
    this.query = ""
  }

  handleDeleteBook(book:Book){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message+"?")
    if(option){
      this.bookService.deleteBook(book).subscribe({
        next:(data:any)=>{
          if (data.code == "200"){
            const message = this.translate.instant("booksManagement.Book-is-deleted")
            alert(message);
            this.fetchBookList();
            this.handleClear();
            this.cdr.markForCheck()
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
        }
      })
    }
  }

  fetchSearchBook(page: Page = this.resultPage){
    this.isSearch = true;
    this.bookService.searchBook(this.query, page.number).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.searchBooks = data.data.content
          this.isSearch = false;
          this.resultPage = data.data
          if(data.data.totalElements == 0){
            this.isBookFound = false
          }
          else{
            this.isBookFound = true
          }
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
  }

  fetchBookList(page:Page = this.bookListPage){
    this.isLoadingBookList = true;
    this.bookService.getAllBooks(page.number, 15).subscribe({
      next:(data: any) => {
        if(data.code == "200"){
          this.bookList= data.data.content;
          this.bookListPage = data.data;
          this.isLoadingBookList = false;
          this.totalBooks = data.data.totalElements;
          this.cdr.markForCheck();
        }
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchBookList()
    }
  }

  close(){
    this.onClose.emit();
  }
  selectBook(book: Book){
    this.onSelect.emit(book);
  }
}
