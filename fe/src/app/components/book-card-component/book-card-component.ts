import { Component, Input } from '@angular/core';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';
import { errorImage } from '../../../assets/constants';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-book-card-component',
  imports: [TranslateModule],
  templateUrl: './book-card-component.html',
  styleUrl: './book-card-component.css',
})
export class BookCardComponent {

  @Input({ required: true })
  book!: Book;

  @Input()
  accessible: boolean = true;

  backendUrl = environment.apiUrl;

  getWebpCover(coverUrl: string | null | undefined): string {
    if (!coverUrl) {
      return 'default.webp';
    }

    return coverUrl.replace(/\.(jpg|jpeg|png)$/i, '.webp');
  }

  onImageError(event: Event): void {
    const image = event.target as HTMLImageElement;

    image.onerror = null;
    image.src = errorImage;
  }
}