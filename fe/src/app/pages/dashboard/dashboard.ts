import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { Post } from '../../models/post';
import { PostsService } from '../../services/posts-service/posts-service';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { Borrow } from '../../models/borrow';
import { MiniPostCardComponent } from '../../components/mini-post-card-component/mini-post-card-component';
import { GenreService } from '../../services/genre-service/genre-service';
import { BookService } from '../../services/book-service/book-service';
import { Genre } from '../../models/genre';

@Component({
  selector: 'app-dashboard',
  imports: [TranslateModule, NavbarComponent, ChartComponent, MiniPostCardComponent, BorrowCardComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  posts:Post[] = [];
  borrows:Borrow[] = [];

  genres:Genre[] = [];
  bookCountByGenre: { [key: string]: number } = {};
  borrowedBookCountByGenre: { [key: string]: number } = {};
  bookGenreLabels: string[] = [];
  bookGenreValues: number[] = [];
  borrowedBookGenreValues: number[] = [];

  constructor(
    public langService: LanguageService,
    private postsService: PostsService,
    private borrowService: BorrowService,
    private genreService: GenreService,
    private bookService: BookService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) 
  {}

  fetchBookCountsByGenre(): void {
    this.genreService.getAllGenres().subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.genres = data.data.content;
                let bookCompleted = 0;
                let borrowCompleted = 0;

                for (const genre of this.genres) {
                    this.bookService.getBooksByGenre(genre.name).subscribe({
                        next: (bookData: any) => {
                            if (bookData.code == "200") {
                                this.bookCountByGenre[genre.name] = bookData.data.totalElements;
                                bookCompleted++;

                                if (bookCompleted === this.genres.length) {
                                    this.bookGenreLabels = Object.keys(this.bookCountByGenre);
                                    this.bookGenreValues = Object.values(this.bookCountByGenre);
                                    this.cdr.markForCheck();
                                }
                            }
                        },
                        error: (err) => console.error(err)
                    });

                    this.bookService.getBorrowedBooksByGenre(genre.name).subscribe({
                        next: (borrowData: any) => {
                            if (borrowData.code == "200") {
                                this.borrowedBookCountByGenre[genre.name] = borrowData.data.totalElements;
                                borrowCompleted++;

                                if (borrowCompleted === this.genres.length) {
                                    this.borrowedBookGenreValues = Object.values(this.borrowedBookCountByGenre);
                                    this.cdr.markForCheck();
                                }
                            }
                        },
                        error: (err) => console.error(err)
                    });
                }
            }
        },
        error: (err) => console.error(err)
    });
  }

  fetchBorrowsByUserId():void{
    const userId = JSON.parse(localStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getBorrowsByUserId(userId).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrows = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  fetchAllPosts():void{
    this.postsService.getAllPost(0, 5).subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllPosts()
      this.fetchBorrowsByUserId()
      this.fetchBookCountsByGenre()
    }
  }

  onPostLikeToggled(updatedPost: Post) {
    const index = this.posts.findIndex(p => p.id === updatedPost.id);
    if (index !== -1) {
        this.posts[index] = updatedPost;
        this.cdr.markForCheck();
    }
  }

  get booksCountByGenreKeys(): string[] {
    return Object.keys(this.bookCountByGenre);
  }

  get booksCountByGenreValues(): number[] {
    return Object.values(this.bookCountByGenre);
  }
}
