import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Page } from '../../models/page';
import { Book } from '../../models/book';
import { BookCardComponent } from '../book-card-component/book-card-component';
import { TranslateModule } from '@ngx-translate/core';
import { PagesComponent } from '../pages-component/pages-component';

@Component({
  selector: 'app-book-list-component',
  imports: [BookCardComponent, TranslateModule, PagesComponent],
  templateUrl: './book-list-component.html',
  styleUrl: './book-list-component.css',
})
export class BookListComponent implements OnChanges{
  @Input({required: true}) books: Book[] = []
  @Input({required: true}) page!: Page
  @Input() selectable: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<Page>();
  @Output() onSelect = new EventEmitter<Book>();

  constructor(
    private cdr: ChangeDetectorRef
  ){}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['page'] && this.page) {
      this.cdr.markForCheck();
    }
  }

  close(){
    this.onClose.emit();
  }

  changePage(newPage: Page) {
    this.onChange.emit(newPage);
  }

  selectBook(book: Book){
    this.onSelect.emit(book);
  }
}
