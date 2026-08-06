import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ExportService } from '../../../services/export-service/export-service';
import { UserService } from '../../../services/user-service/user-service';
import { User } from '../../../models/user';
import { Page } from '../../../models/page';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../../util/error-notification';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../../components/loading-component/loading-component';
import { PagesComponent } from "../../../components/pages-component/pages-component";
import { BookService } from '../../../services/book-service/book-service';
import { Book } from '../../../models/book';

const defaultPage: Page = {
  first: true,
  last: true,
  number: 0,
  totalPages: 0,
  totalElements: 0,
  numberOfElements: 0
}

@Component({
  selector: 'app-export-books-form',
  imports: [LoadingComponent, TranslateModule, PagesComponent],
  templateUrl: './export-books-form.html',
  styleUrl: './export-books-form.css',
})
export class ExportBooksForm {
  @Output() onClose = new EventEmitter<void>()

  isLoading:boolean = true
  books:Book[] = []
  booksPage:Page = defaultPage

  selectedBooks:Book[] = []

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private exportService: ExportService,
    private bookService: BookService
  ){}

  handleExport(){
    if(this.books){
      const message = this.translate.instant("export.Confirm-export")
      const option = confirm(message+"?")
      if(!option) return;
      this.exportService.exportBook(this.selectedBooks)
    }
  }

  handleUnselectAll(){
    this.selectedBooks = []
    this.cdr.markForCheck()
  }
  handleSelectAll(){
    const totalBooks = this.booksPage.totalElements
    this.bookService.getAllBooks(0, totalBooks).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.selectedBooks = data.data.content
          this.cdr.markForCheck();
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading = false;
        this.cdr.markForCheck()
      }
    })
  }

  fetchAllBooks(page: Page = this.booksPage, limit:number = 10){
    this.isLoading = true;
    this.bookService.getAllBooks(page.number, limit).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.books = data.data.content
          this.booksPage = data.data
          this.isLoading = false;
          this.cdr.markForCheck()
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading = false;
        this.cdr.markForCheck()
      }
    })
  }

  toggleSelectedUser(book:Book){
    if (this.selectedBooks.some(u => u.id === book.id)) {
      this.selectedBooks = this.selectedBooks.filter(u => u.id !== book.id);
    } else {
      this.selectedBooks.push(book);
    }
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllBooks();
    }
  }
}
