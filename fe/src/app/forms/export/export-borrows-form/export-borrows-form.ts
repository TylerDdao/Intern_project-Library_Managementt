import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ExportService } from '../../../services/export-service/export-service';
import { Page } from '../../../models/page';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../../util/error-notification';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../../components/loading-component/loading-component';
import { PagesComponent } from "../../../components/pages-component/pages-component";
import { BorrowService } from '../../../services/borrow-service/borrow-service';
import { Borrow } from '../../../models/borrow';
import { formatCurrency, formatTime } from '../../../util/format-number';
import { FormsModule } from '@angular/forms';

const defaultPage: Page = {
  first: true,
  last: true,
  number: 0,
  totalPages: 0,
  totalElements: 0,
  numberOfElements: 0
}

@Component({
  selector: 'app-export-borrows-form',
  imports: [LoadingComponent, TranslateModule, PagesComponent, FormsModule],
  templateUrl: './export-borrows-form.html',
  styleUrl: './export-borrows-form.css',
})
export class ExportBorrowsForm {
  @Output() onClose = new EventEmitter<void>()

  isLoading:boolean = true
  borrows:Borrow[] = []
  borrowsPage:Page = defaultPage

  selectedBorrows:Borrow[] = []

  query:string = ""

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private exportService: ExportService,
    private borrowService: BorrowService
  ){}

  isAllSelected(): boolean {
    return this.selectedBorrows.length === this.borrowsPage.totalElements;
  }

  handleSearch(){
    console.log("SEARCH")
    if(this.query){
      this.isLoading = true;
      this.borrowService.searchBorrows(this.query, this.borrowsPage.number).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            this.borrows = data.data.content;
            this.borrowsPage = data.data;
            this.isLoading = false;
            this.cdr.markForCheck();
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
          this.isLoading = false
          this.cdr.markForCheck()
        }
      })
    }
  }

  handleClearSearch(){
    this.query = ""
    this.fetchAllborrows();
    this.cdr.markForCheck()
  }

  handleExport(){
    if(this.borrows){
      const message = this.translate.instant("export.Confirm-export")
      const option = confirm(message+"?")
      if(!option) return;
      this.exportService.exportBorrow(this.selectedBorrows)
    }
  }

  handleUnselectAll(){
    this.selectedBorrows = []
    this.cdr.markForCheck()
  }
  handleSelectAll(){
    const totalBorrows = this.borrowsPage.totalElements
    this.borrowService.getAllBorrow(0, totalBorrows).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.selectedBorrows = data.data.content
          this.cdr.markForCheck();
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading = false;
        this.cdr.markForCheck()
      }
    })
  }

  fetchAllborrows(page: Page = this.borrowsPage){
    this.isLoading = true;
    this.borrowService.getAllBorrow(page.number).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.borrows = data.data.content
          this.borrowsPage = data.data
          this.isLoading = false;
          this.cdr.markForCheck()
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading = false;
        this.cdr.markForCheck()
      }
    })
  }

  getPenaltyFee(borrow:Borrow){
    if(borrow.penalty){
      return formatCurrency(borrow.penalty)
    }
    else{
      return ""
    }
  }

  getDueDate(borrow:Borrow){
    if(borrow.dueDate){
      return formatTime(borrow.dueDate);
    }
    else{
      return ""
    }
  }

  getCreatedAt(borrow:Borrow){
    if(borrow.dueDate){
      return formatTime(borrow.createdAt);
    }
    else{
      return ""
    }
  }

  toggleSelectedBorrow(borrow:Borrow){
    if (this.selectedBorrows.some(b => b.id === borrow.id)) {
      this.selectedBorrows = this.selectedBorrows.filter(b => b.id !== borrow.id);
    } else {
      this.selectedBorrows.push(borrow);
    }
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllborrows();
    }
  }
}
