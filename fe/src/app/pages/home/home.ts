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
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, MiniPostCardComponent, BorrowCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  posts:Post[] = [];
  borrows:Borrow[] = [];

  genres:Genre[] = [];
  bookCountByGenre: { [key: string]: number } = {};
  borrowsCountByGenre: { [key: string]: number } = {};

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

  private fetchBooksCountByGenre(): void {
    this.bookService.getBooksCountByGenre().subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.bookCountByGenre = data.data;
                this.cdr.markForCheck();
            }
        },
        error: (err) => console.error(err)
    });
  }

  get booksCountByGenreLabels(): string[] {
      return Object.keys(this.bookCountByGenre);
  }

  get booksCountByGenreValues(): number[] {
      return Object.values(this.bookCountByGenre);
  }

  private fetchBorrowsCountByGenre(): void {
    this.borrowService.getBorrowsCountByGenre().subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.borrowsCountByGenre = data.data;
                this.cdr.markForCheck();
            }
        },
        error: (err) => console.error(err)
    });
  }

  get borrowsCountByGenreLabels(): string[] {
      return Object.keys(this.borrowsCountByGenre);
  }

  get borrowsCountByGenreValues(): number[] {
      return Object.values(this.borrowsCountByGenre);
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
      this.fetchBooksCountByGenre()
      this.fetchBorrowsCountByGenre();
    }
  }

  onPostLikeToggled(updatedPost: Post) {
    const index = this.posts.findIndex(p => p.id === updatedPost.id);
    if (index !== -1) {
        this.posts[index] = updatedPost;
        this.cdr.markForCheck();
    }
  }
}
