import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { Page } from '../../models/page';
import { Book } from '../../models/book';
import { BookCardComponent } from '../book-card-component/book-card-component';
import { TranslateModule } from '@ngx-translate/core';
import { PagesComponent } from '../pages-component/pages-component';
import { BookService } from '../../services/book-service/book-service';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../loading-component/loading-component';

@Component({
  selector: 'app-book-list-component',
  imports: [BookCardComponent, TranslateModule, PagesComponent, LoadingComponent],
  templateUrl: './book-list-component.html',
  styleUrl: './book-list-component.css',
})
export class BookListComponent{
  @Input() selectable: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<Page>();
  @Output() onSelect = new EventEmitter<Book>();

  constructor(
    private cdr: ChangeDetectorRef,
    private bookService: BookService,
    @Inject(PLATFORM_ID) private platformId: Object
  ){}

  bookList: Book[] = [];
  bookListPage: Page = {
    number: 0,
    totalPages: 1,
    last: true,
    first: true
  }
  isLoadingBookList: boolean = true;

  totalBooks:number = 0;

  fetchBookList(page:Page = this.bookListPage){
    this.isLoadingBookList = true;
    this.bookService.getAllBooks(page.number, 10).subscribe({
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

  // ngOnChanges(changes: SimpleChanges) {
  //   if (changes['page'] && this.page) {
  //     this.cdr.markForCheck();
  //   }
  // }

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
