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
  selector: 'app-borrows-management',
  imports: [TranslateModule, NavbarComponent, SortSideBarComponent, BorrowCardComponent, PagesComponent],
  templateUrl: './borrows-management.html',
  styleUrl: './borrows-management.css',
})
export class BorrowsManagement {
  passedDueBorrows: Borrow[] = [];
  passedDueBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }
  nearDueBorrows: Borrow[] = [];
  nearDueBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }
  onGoingBorrows: Borrow[] = [];
  onGoingBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  returnedBorrows: Borrow[] = [];
  returnedBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  constructor(
    private borrowService: BorrowService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  fetchReturnedBorrows(page: Page = this.returnedBorrowsPages):void{
    this.borrowService.getBorrowByStatus("returned", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.returnedBorrows = data.data.content;
          this.returnedBorrowsPages = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchOnGoingBorrows(page: Page = this.onGoingBorrowsPages):void{
    this.borrowService.getBorrowByStatus("on-going", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.onGoingBorrows = data.data.content;
          this.onGoingBorrowsPages = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchNearDueBorrows(page: Page = this.nearDueBorrowsPages):void{
    this.borrowService.getBorrowByStatus("near", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.nearDueBorrows = data.data.content;
          this.nearDueBorrowsPages = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }
  fetchLateBorrows(page: Page = this.passedDueBorrowsPages):void{
    this.borrowService.getBorrowByStatus("late", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.passedDueBorrows = data.data.content;
          this.passedDueBorrowsPages = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchReturnedBorrows()
      this.fetchOnGoingBorrows()
      this.fetchNearDueBorrows()
      this.fetchLateBorrows()
    }
  }
}
