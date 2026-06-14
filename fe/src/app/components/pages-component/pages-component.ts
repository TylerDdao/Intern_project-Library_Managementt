import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page } from '../../models/page';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-pages-component',
  imports: [TranslateModule],
  templateUrl: './pages-component.html',
  styleUrl: './pages-component.css',
})
export class PagesComponent {
  @Input({required: true}) pages!:Page

  @Output() onApply = new EventEmitter<Page>();

  number: number = 0
  last:boolean = true
  first: boolean = true
  totalPages: number = 1;

  apply(change:number): void {
    this.onApply.emit({
      number: this.number + change,
      last: this.last,
      first: this.first,
      totalPages: this.totalPages
    });
  }
}
