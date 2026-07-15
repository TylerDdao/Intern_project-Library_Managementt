import { ChangeDetectorRef, Component, EventEmitter, Input, input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Borrow } from '../../models/borrow';
import { BorrowCardComponent } from '../borrow-card-component/borrow-card-component';
import { TranslateModule } from '@ngx-translate/core';
import { Page } from '../../models/page';
import { PagesComponent } from '../pages-component/pages-component';

@Component({
  selector: 'app-borrow-list-component',
  imports: [BorrowCardComponent, TranslateModule, PagesComponent],
  templateUrl: './borrow-list-component.html',
  styleUrl: './borrow-list-component.css',
})
export class BorrowListComponent implements OnChanges {
  @Input({required: true}) borrows: Borrow[] = []
  @Input({required: true}) page!: Page
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<Page>();

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
}
