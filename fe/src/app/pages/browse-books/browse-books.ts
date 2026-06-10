import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';
import { BookService } from '../../services/book-service/book-service';
import { isPlatformBrowser } from '@angular/common';
import { Genre } from '../../models/genre';
import { GenreService } from '../../services/genre-service/genre-service';
import { EMPTY, firstValueFrom } from 'rxjs';
import { expand, reduce } from 'rxjs/operators';

@Component({
  selector: 'app-browse-books',
  imports: [NavbarComponent, SortSideBarComponent, BookCardComponent, TranslateModule],
  templateUrl: './browse-books.html',
  styleUrl: './browse-books.css',
})
export class BrowseBooks {
  constructor(
    public langService: LanguageService,
    private bookService: BookService,
    private genreService: GenreService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef
  ) 
  {}

  mostPostsBooks:Book[] = []
  genres: string[] = []

  searchBooks: Book[] = []
  isSearch:boolean = false;
  isBookFound:boolean = true;

  handleApply(query: SideBarQuery): void {
    if(query.isClear){
      this.isSearch = false;
      this.searchBooks = [];
      this.isBookFound = false;
      return;
    }
    this.isSearch = true;
    this.searchBooks = [];
    this.isBookFound = true;
    this.bookService.getBooksByQuery(query).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          if(data.data.totalElements > 0){
            this.searchBooks = data.data.content;
            this.cdr.detectChanges();
          }
          else{
            this.isBookFound = false;
            this.cdr.detectChanges();
          }
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  async fetchAllGenres(): Promise<void> {
    let isLast = false;
    let page = 0;
    const collectedGenres: Genre[] = [];

    try {
      while (!isLast) {
        const data: any = await firstValueFrom(this.genreService.getAllGenres(page, 50));        
        if (data && data.code === "200") {
          collectedGenres.push(...data.data.content);           
          isLast = data.data.last; 
          
          if (!isLast) {
            page++;
          }
        } else {
          break; 
        }
      }
      
      this.genres = collectedGenres.map(genre => genre.name);
      this.cdr.detectChanges();
    } catch (err) {
      console.error("Failed to load genres", err);
    }
  }

  // fetchAllBooks():void{
  //   this.bookService.getAllBooks().subscribe({
  //     next: (data:any) => {
  //       if(data.code == "200"){
  //         this.books = data.data.content;
  //         this.cdr.detectChanges();
  //       }
  //     },
  //     error: (err) => {
  //       console.error(err);
  //     }
  //   })
  // }

  fetchMostPostsBooks():void{
    this.bookService.getMostPostsBooks().subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.mostPostsBooks = data.data.content;
          this.cdr.detectChanges();
        }
      },
      error: (err) =>{
        console.error(err)
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllGenres();
      this.fetchMostPostsBooks();
    }
  }
}
