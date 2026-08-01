import { Component, Input, SimpleChanges } from '@angular/core';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';
import { backendUrl, errorImage } from '../../../assets/constants';

@Component({
  selector: 'app-book-card-component',
  imports: [TranslateModule],
  templateUrl: './book-card-component.html',
  styleUrl: './book-card-component.css',
})
export class BookCardComponent {
  @Input({ required: true }) book!: Book;
  @Input() accessible: boolean = true

  backendUrl = backendUrl;

  ngOnChanges(changes: SimpleChanges): void {

  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = errorImage;
  }
}
