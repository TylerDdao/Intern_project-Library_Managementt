import { Component, Input } from '@angular/core';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-book-card-component',
  imports: [TranslateModule],
  templateUrl: './book-card-component.html',
  styleUrl: './book-card-component.css',
})
export class BookCardComponent {
  @Input({ required: true }) book!: Book;
}
