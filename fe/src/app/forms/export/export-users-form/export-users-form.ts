import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ExportService } from '../../../services/export-service/export-service';
import { UserService } from '../../../services/user-service/user-service';
import { User } from '../../../models/user';
import { Page } from '../../../models/page';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../../util/error-notification';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../../components/loading-component/loading-component';
import { PagesComponent } from "../../../components/pages-component/pages-component";

const defaultPage: Page = {
  first: true,
  last: true,
  number: 0,
  totalPages: 0,
  totalElements: 0,
  numberOfElements: 0
}

@Component({
  selector: 'app-export-users-form',
  imports: [LoadingComponent, TranslateModule, PagesComponent],
  templateUrl: './export-users-form.html',
  styleUrl: './export-users-form.css',
})
export class ExportUsersForm {
  @Output() onClose = new EventEmitter<void>()

  isLoading:boolean = true
  users:User[] = []
  usersPage:Page = defaultPage

  selectedUsers:User[] = []

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private exportService: ExportService,
    private userService: UserService
  ){}

  handleExport(){
    if(this.users){
      const message = this.translate.instant("export.Confirm-export")
      const option = confirm(message+"?")
      if(!option) return;
      this.exportService.exportUsers(this.selectedUsers)
    }
  }

  handleUnselectAll(){
    this.selectedUsers = []
    this.cdr.markForCheck()
  }
  handleSelectAll(){
    const totalUsers = this.usersPage.totalElements
    this.userService.getAllUser(0, totalUsers).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.selectedUsers = data.data.content
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

  fetchAllUsers(page: Page = this.usersPage){
    this.isLoading = true;
    this.userService.getAllUser(page.number).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.users = data.data.content
          this.usersPage = data.data
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

  toggleSelectedUser(user:User){
    if (this.selectedUsers.some(u => u.id === user.id)) {
      this.selectedUsers = this.selectedUsers.filter(u => u.id !== user.id);
    } else {
      this.selectedUsers.push(user);
    }
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllUsers();
    }
  }
}
