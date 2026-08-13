import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule, TranslateService } from '@ngx-translate/core';
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
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement } from '../../models/announcement';
import { webAnnouncements } from '../../../assets/constants';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, MiniPostCardComponent, BorrowCardComponent, PagesComponent, LoadingComponent, AnnouncementComponent],
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


  isLoading:boolean = true
  pendingRequests:number =0;

  announcements:Announcement[] = []

  constructor(
    public langService: LanguageService,
    private postService: PostService,
    private borrowService: BorrowService,
    private bookService: BookService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    private announcementService: AnnouncementService
  ) 
  {}

  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
  }

  private startLoading() {
    this.pendingRequests++;
    this.isLoading = true;
  }

  private finishLoading() {
    this.pendingRequests--;
    if (this.pendingRequests <= 0) {
      this.pendingRequests = 0;
      this.isLoading = false;
      this.cdr.markForCheck();
    }
  }


  private fetchBooksCountByGenre(): void {
    this.startLoading()
    this.bookService.getBooksCountByGenre().subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.bookCountByGenre = data.data;
                this.cdr.markForCheck();
            }
            this.finishLoading()
        },
        error: (err:HttpErrorResponse) => {
          errorNoti(err, this.translate)
        }
    });
  }

  get booksCountByGenreLabels(): string[] {
      return Object.keys(this.bookCountByGenre);
  }

  get booksCountByGenreValues(): number[] {
      return Object.values(this.bookCountByGenre);
  }

  private fetchBorrowsCountByGenre(): void {
    this.startLoading()
    this.borrowService.getBorrowsCountByGenre().subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.borrowsCountByGenre = data.data;
                this.cdr.markForCheck();
            }
            this.finishLoading()
        },
        error: (err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
          this.finishLoading()
        }
    });
  }

  get borrowsCountByGenreLabels(): string[] {
      return Object.keys(this.borrowsCountByGenre);
  }

  get borrowsCountByGenreValues(): number[] {
      return Object.values(this.borrowsCountByGenre);
  }

  fetchBorrowsByUserId(page: Page = this.borrowsPage):void{
    this.startLoading()
    const userId = JSON.parse(sessionStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getMyBorrows(true, null, page.number, 10).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrows = data.data.content;
          this.borrows = this.borrows.filter(borrow => borrow.active)
          this.cdr.markForCheck();
        }
        this.finishLoading()
      },
      error: (err:HttpErrorResponse) => {
        errorNoti(err, this.translate)
        this.finishLoading()
      }
    })
  }

  fetchMostLikesPosts():void{
    this.startLoading()
    this.postService.getMostLikesPosts(0, 5).subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.cdr.markForCheck();
        }
        this.finishLoading()
      },
      error: (err:HttpErrorResponse) => {
        errorNoti(err, this.translate)
        this.finishLoading()
      }

    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements();
      // this.announcements = [
      //   {
      //     id: 1,
      //     type: 'info',
      //     subject_vi: "Ý kiến của bạn rất quan trọng!",
      //     content_vi: "Chúng tôi rất mong nhận được phản hồi từ bạn. Vui lòng gửi ý kiến cho chúng tôi qua liên kết bên dưới!",

      //     subject_en: 'Your feedback matters!',
      //     content_en: 'We are excited to hear your feedback. Please send it to us via the link below!',

      //     link: 'https://forms.gle/8agsuPwmFonKSzPb6',
      //     linkText_vi: 'Biểu mẫu góp ý',
      //     linkText_en: 'Feedback Form',
      //     isActive: true,
      //     locations: ['']
      //   },
      //   {
      //     id: 2,
      //     type: 'warning',
      //     subject_vi: 'Website đang phát triển — Một số trang hiện chưa khả dụng',
      //     content_vi: 'Website này vẫn đang trong quá trình phát triển nên một số trang chưa thể truy cập vào lúc này. Chúng tôi sẽ hoàn thiện sớm nhất có thể!',

      //     subject_en: 'Site Under Development — Some pages are not available',
      //     content_en: 'This website is still under active development so some pages are not available to access at the moment. We will finish them as soon as possible!',
      //     isActive: true,
      //     locations: ['']
      //   },
      // ];

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
