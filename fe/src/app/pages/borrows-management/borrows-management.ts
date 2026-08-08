import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule } from '@ngx-translate/core';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { isPlatformBrowser } from '@angular/common';
import { Borrow } from '../../models/borrow';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { Page } from '../../models/page';
import { BorrowListComponent } from '../../components/borrow-list-component/borrow-list-component';
import { BorrowPolicyForm } from '../../forms/borrow-policy-form/borrow-policy-form';
import { ExportBorrowsForm } from '../../forms/export/export-borrows-form/export-borrows-form';

@Component({
  selector: 'app-borrows-management',
  imports: [TranslateModule, NavbarComponent, SortSideBarComponent, BorrowCardComponent, PagesComponent, BorrowListComponent, BorrowPolicyForm, ExportBorrowsForm],
  templateUrl: './borrows-management.html',
  styleUrl: './borrows-management.css',
})
export class BorrowsManagement {
  isBorrowPolicyOpen: boolean = false;
  isExportBorrow: boolean = false

  isSearch: boolean = false;

  result: Borrow[] = []
  query:  SideBarQuery | null = null;
  lastQuery: SideBarQuery | null = null;
  isLoadingResult:boolean = true;
  resultPages: Page = {
    last: true, 
    first: true,
    number: 0,
    totalPages: 1
  }

  passedDueBorrows: Borrow[] = [];
  isLoadingPassedDueBorrows: boolean = true;
  passedDueBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }
  nearDueBorrows: Borrow[] = [];
  isLoadingNearDueBorrows: boolean = true;
  nearDueBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }
  onGoingBorrows: Borrow[] = [];
  isLoadingOnGoingBorrows: boolean = true;
  onGoingBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  activeBorrows: Borrow[] = [];
  isLoadingActiveBorrows:boolean = true;
  activeBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  returnedBorrows: Borrow[] = [];
  isLoadingReturnedBorrows:boolean = true;
  returnedBorrowsPages: Page = {
    last: true,
    first: true,
    number: 0,
    totalPages: 1
  }

  isOpenActiveList: boolean = false;
  isOpenReturnedList: boolean = false;

  constructor(
    private borrowService: BorrowService,
    protected cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  handleCloseExportBorrow(){
    this.isExportBorrow = false;
  }

  handleCloseBorrowPolicy(){
    this.isBorrowPolicyOpen = false;
    this.cdr.markForCheck();
  }

  handleCloseList(){
    this.isOpenActiveList = false;
    this.isOpenReturnedList = false;
    this.cdr.markForCheck()
  }

  handleOpenActiveList() {
    this.fetchActiveBorrows();
  }

  handleSearch(query: SideBarQuery){
    this.isSearch = true;
    if(query.isClear){
      this.isSearch = false;
      this.query = null;
      return
    }
    this.isSearch = true;
    this.query = query;
    this.fetchResult(this.resultPages);
  }

  fetchResult(page: Page = this.resultPages): void{
    if(this.query){
      this.borrowService.getBorrowsByQuery(this.query, page.number).subscribe({
        next: (data: any) => {
          if(data.code == "200"){
            this.result = data.data.content;
            this.resultPages = data.data;
            this.isLoadingResult = false;
            this.cdr.markForCheck();
          }
        },
        error: (err) => {
          console.error(err);
        }
      })
    }
  }

  fetchActiveBorrows(page: Page = this.activeBorrowsPages):void{
    this.borrowService.getAllActiveBorrows(page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.activeBorrows = data.data.content;
          this.activeBorrowsPages = data.data;
          this.isLoadingActiveBorrows = false;
          this.isOpenActiveList = true;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  fetchReturnedBorrows(page: Page = this.returnedBorrowsPages):void{
    this.borrowService.getBorrowByStatus("returned", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.returnedBorrows = data.data.content;
          this.returnedBorrowsPages = data.data;
          this.isLoadingReturnedBorrows = false;
          this.isOpenReturnedList = true;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  fetchOnGoingBorrows(page: Page = this.onGoingBorrowsPages):void{
    this.borrowService.getBorrowByStatus("on-going", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.onGoingBorrows = data.data.content;
          this.onGoingBorrowsPages = data.data;
          this.isLoadingOnGoingBorrows = false;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  fetchNearDueBorrows(page: Page = this.nearDueBorrowsPages):void{
    this.borrowService.getBorrowByStatus("near", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.nearDueBorrows = data.data.content;
          this.nearDueBorrowsPages = data.data;
          this.isLoadingNearDueBorrows = false;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }
  fetchLateBorrows(page: Page = this.passedDueBorrowsPages):void{
    this.borrowService.getBorrowByStatus("late", page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.passedDueBorrows = data.data.content;
          this.passedDueBorrowsPages = data.data;
          this.isLoadingPassedDueBorrows = false;
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
      this.fetchOnGoingBorrows()
      this.fetchNearDueBorrows()
      this.fetchLateBorrows()
    }
  }
}
