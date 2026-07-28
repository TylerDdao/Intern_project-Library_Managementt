import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { Post } from '../../models/post';
import { PostService } from '../../services/post-service/post-service';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { Borrow } from '../../models/borrow';
import { MiniPostCardComponent } from '../../components/mini-post-card-component/mini-post-card-component';
import { GenreService } from '../../services/genre-service/genre-service';
import { BookService } from '../../services/book-service/book-service';
import { Genre } from '../../models/genre';
import { PagesComponent } from "../../components/pages-component/pages-component";
import { Page } from '../../models/page';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, MiniPostCardComponent, BorrowCardComponent, PagesComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  posts:Post[] = [];
  borrows:Borrow[] = [];

  genres:Genre[] = [];
  bookCountByGenre: { [key: string]: number } = {};
  borrowsCountByGenre: { [key: string]: number } = {};

  borrowsPage:Page = {
      first: true,
      last: true,
      number: 0,
      totalPages: 1
    }

  isLoading: {[key:string]:boolean} = {
    "borrows": true,
    "topPosts": true
  };

  constructor(
    public langService: LanguageService,
    private postService: PostService,
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

  fetchBorrowsByUserId(page: Page = this.borrowsPage):void{
    const userId = JSON.parse(sessionStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getMyBorrows(true, null, page.number, 10).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrows = data.data.content;
          this.borrows = this.borrows.filter(borrow => borrow.active)
          this.isLoading["borrows"] = false;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err)
      }
    })
  }

  fetchMostLikesPosts():void{
    this.postService.getMostLikesPosts(0, 5).subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.isLoading["topPosts"] = false;
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
      this.fetchMostLikesPosts()
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
