import { Component, Input, SimpleChanges } from '@angular/core';
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
  @Input() accessible: boolean = true

  bookCover = ''

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['book'] && this.book) {
        this.bookCover = this.book.title.replaceAll(' ', '-').toLowerCase();
    }
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }
}
