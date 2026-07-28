import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule } from '@ngx-translate/core';
import { SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { isPlatformBrowser } from '@angular/common';
import { Borrow } from '../../models/borrow';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { Page } from '../../models/page';

@Component({
  selector: 'app-my-borrows',
  imports: [TranslateModule, NavbarComponent, SortSideBarComponent, BorrowCardComponent, PagesComponent],
  templateUrl: './my-borrows.html',
  styleUrl: './my-borrows.css',
})
export class MyBorrows {
  borrowing:Borrow[] = [];
  borrowsHistory: Borrow[] = [];

  borrowingPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  borrowsHistoryPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  passedDueBorrows: Borrow[] = [];
  nearDueBorrows: Borrow[] = [];
  otherBorrows: Borrow[] = [];

  isLoading: {[key:string]:boolean} = {
    "borrowing": true,
    "borrowHistory": true
  };

  constructor(
    private borrowService: BorrowService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  fetchBorrowsByUserId(page: Page = this.borrowingPages):void{
    const userId = JSON.parse(sessionStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getMyBorrows(true, null, page.number, 5).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrowing = data.data.content;
          this.borrowingPages = data.data;
          this.isLoading["borrowing"] = false
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchBorrowsHistoryByUserId(page: Page = this.borrowsHistoryPages):void{
    const userId = JSON.parse(sessionStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getMyBorrows(false,null, page.number, 5).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrowsHistory = data.data.content;
          this.borrowsHistoryPages = data.data;
          this.isLoading["borrowHistory"] = false;
          this.cdr.markForCheck();
        }
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchBorrowsByUserId()
      this.fetchBorrowsHistoryByUserId()
    }
  }
}
