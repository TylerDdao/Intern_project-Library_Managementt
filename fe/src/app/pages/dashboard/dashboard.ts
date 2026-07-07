import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { Post } from '../../models/post';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { GenreService } from '../../services/genre-service/genre-service';
import { BookService } from '../../services/book-service/book-service';
import { Book } from '../../models/book';
import { Page } from '../../models/page';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { Borrow } from '../../models/borrow';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';

@Component({
  selector: 'app-dashboard',
  imports: [TranslateModule, NavbarComponent, ChartComponent, BookCardComponent, PagesComponent, BorrowCardComponent],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  borrowStatusLabels: string[] = ['Late', 'Near Due', 'On-Time'];
  borrowStatusValues: number[] = [0, 0, 0];
  lateBorrowCount = 0;
  nearBorrowCount = 0;
  onTimeBorrowCount = 0;

  borrowsCountByGenre:{ [key: string]: number } = {};
  booksCountByGenre:{ [key: string]: number } = {};

  unavailableBooks:Book[] = [];
  unavailableBooksPage:Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  }

  lateBorrows: Borrow[] = []
  lateBorrowsPage:Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  }

  isLoading: {[key: string]: boolean} = {
    "lateBorrows": true,
    "unavailableBooks": true
  };

  constructor(
    public langService: LanguageService,
    private borrowService: BorrowService,
    private bookService: BookService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) 
  {}

  fetchLateBorrow(page:Page = this.lateBorrowsPage):void{
    this.borrowService.getBorrowByStatus("late", page.number).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.lateBorrows = data.data.content;
          this.lateBorrowsPage = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchUnavailableBooks(page:Page = this.unavailableBooksPage):void{
    this.bookService.getUnavailableBooks(page.number).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.isLoading["unavailableBooks"] = false
          this.unavailableBooks = data.data.content;
          this.unavailableBooksPage = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchLateBorrows():void{
    this.borrowService.getBorrowByStatus("late").subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.isLoading["lateBorrows"] = false
          this.lateBorrowCount = data.data.totalElements
          this.updateChartData()
          this.cdr.markForCheck()
        }
      }
    })
  }

  fetchNearBorrows():void{
    this.borrowService.getBorrowByStatus("near").subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.nearBorrowCount = data.data.totalElements
          this.updateChartData()
          this.cdr.markForCheck()
        }
      }
    })
  }

  fetchOnTimeBorrows():void{
    this.borrowService.getBorrowByStatus("onTime").subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          this.onTimeBorrowCount = data.data.totalElements
          this.updateChartData()
          this.cdr.markForCheck()
        }
      }
    })
  }

  private updateChartData(): void {
    this.borrowStatusValues = [
      this.lateBorrowCount,
      this.nearBorrowCount,
      this.onTimeBorrowCount
    ];

    console.log(this.borrowStatusValues)
    this.cdr.markForCheck();
  }

  private fetchBorrowsByGenre(): void {
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
  
  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchOnTimeBorrows();
      this.fetchNearBorrows();
      this.fetchLateBorrows();
      this.fetchBorrowsByGenre();
      this.fetchUnavailableBooks();
      this.fetchLateBorrow();
    }
  }
}
