import { ChangeDetectorRef, Component, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Book } from '../../models/book';
import { BookService } from '../../services/book-service/book-service';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-book-page',
  imports: [NavbarComponent, BookCardComponent, TranslateModule],
  templateUrl: './book-page.html',
  styleUrl: './book-page.css',
})
export class BookPage {
  bookId!: number;
  book!: Book;
  bookCover = '';
  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private cdr: ChangeDetectorRef
  ) {}

  fetchBook(id: number):void{
    this.bookService.getBook(id).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.book = data.data
          this.cdr.markForCheck()
        }
      },
      error(err){
        console.error(err)
      }
    })
  }

  ngOnInit() {
    this.bookId = Number(this.route.snapshot.paramMap.get('book-id'));
    this.fetchBook(this.bookId);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['book'] && this.book) {
        this.bookCover = this.book.title.replaceAll(' ', '-').toLowerCase();
    }
  }
  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }
}
