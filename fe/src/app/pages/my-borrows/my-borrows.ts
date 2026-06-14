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
  borrows:Borrow[] = [];
  borrowPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  passedDueBorrows: Borrow[] = [];
  nearDueBorrows: Borrow[] = [];
  otherBorrows: Borrow[] = [];

  constructor(
    private borrowService: BorrowService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  fetchBorrowsByUserId(page: Page = this.borrowPages):void{
    const userId = JSON.parse(localStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getBorrowsByUserId(userId, page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrows = data.data.content;
          this.borrowPages = data.data;
          this.cdr.markForCheck();
        }
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchBorrowsByUserId()
    }
  }
}
