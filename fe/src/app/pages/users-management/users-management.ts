import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { UserCard } from '../../components/user-card/user-card';
import { User } from '../../models/user';
import { UserService } from '../../services/user-service/user-service';
import { Role } from '../../models/role';
import { isPlatformBrowser } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { NewUserForm } from '../../forms/new-user-form/new-user-form';
import { EditUserForm } from '../../forms/edit-user-form/edit-user-form';
import { NewRoleForm } from '../../forms/new-role-form/new-role-form';
import { RoleListComponent } from '../../components/role-list-component/role-list-component';
import { Feature } from '../../models/feature';
import { FeatureService } from '../../services/feature-service/feature-service';
import { EditRoleForm } from '../../forms/edit-role-form/edit-role-form';
import { first, last } from 'rxjs';
import { Page } from '../../models/page';
import { RoleService } from '../../services/role-service/role-service';
import { ExportService } from '../../services/export-service/export-service';
import { ExportUsersForm } from '../../forms/export/export-users-form/export-users-form';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { LoadingComponent } from "../../components/loading-component/loading-component";
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { PagesComponent } from "../../components/pages-component/pages-component";
import { getUser } from '../../util/session-storage';
import { Announcement } from '../../models/announcement';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { webAnnouncements } from '../../../assets/constants';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';

@Component({
  selector: 'app-users-management',
  imports: [NavbarComponent, UserCard, TranslateModule, NewUserForm, EditUserForm, NewRoleForm, RoleListComponent, EditRoleForm, ExportUsersForm, SortSideBarComponent, LoadingComponent, PagesComponent, AnnouncementComponent],
  templateUrl: './users-management.html',
  styleUrl: './users-management.css',
})
export class UsersManagement {
  canDeleteUser: boolean = false
  roles: Role[] = [];
  features: Feature[] = [];

  users: { role: Role, users: User[] }[] = [];

  isCreateNewUser: boolean = false;
  isEditUser: boolean = false;
  isEditRole: boolean = false;
  isRoleListOpen:boolean = false;
  isCreateNewRole:boolean = false;


  editUser: User | null = null;
  editRole: Role | null = null;

  roleListPage: Page = {
    first: true,
    last: true,
    totalPages: 1,
    number: 0
  }

  isExportUser:boolean = false;

  isSearch: boolean = false;
  result: User[] = []
  query:  SideBarQuery | null = null;
  lastQuery: SideBarQuery | null = null;
  isLoading:boolean = true;
  resultPages: Page = {
    last: true, 
    first: true,
    number: 0,
    totalPages: 1
  }

  private pendingRequests = 0;
  
  constructor(
    private userService: UserService,
    private featureService: FeatureService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    public langService: LanguageService,
    private translate: TranslateService,
    private roleService: RoleService,
    private announcementService: AnnouncementService
  ){}

  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
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
      this.userService.searchUser(this.query.searchQuery, page.number).subscribe({
        next: (data: any) => {
          if(data.code == "200"){
            this.result = data.data.content;
            this.resultPages = data.data;
            
            this.cdr.markForCheck();
          }
          this.isLoading = false;
        },
        error: (err:HttpErrorResponse) => {
          errorNoti(err, this.translate)
          this.isLoading = false;
        }
      })
    }
  }

  handleCloseExportUser(){
    this.isExportUser = false
  }

  handleEditUser(user: User){
    this.editUser = user;
    this.isEditUser = true;
    this.cdr.markForCheck();
  }

  fetchFeatures(){
  this.startLoading()
  this.featureService.getAllFeatures().subscribe({
    next: (data: any) => {
      if(data.code == "200"){
        this.features = data.data.content
        this.finishLoading()
        this.cdr.markForCheck()
      }
      this.finishLoading();
    },
    error: (err:HttpErrorResponse) => {
      errorNoti(err, this.translate)
    }
  })
}

  fetchRolesAndUser(){
    this.startLoading()
    this.users = [];
    this.roleService.getAllRoles().subscribe({
        next: (data: any) => {
          if (data.code == "200") {
            const defaultRole = data.data.content.find((role: Role) => role.default);
            this.roles = data.data.content.sort((a: Role, b: Role) => {
              const order = ['ROLE_ROOT', defaultRole.name];
              const aIndex = order.indexOf(a.name);
              const bIndex = order.indexOf(b.name);

              if (aIndex !== -1 && bIndex !== -1) return aIndex - bIndex;
              if (aIndex !== -1) return -1;
              if (bIndex !== -1) return 1;
              return a.name.localeCompare(b.name);
            });
            // this.roles.forEach(role => {role.name = role.name.replace("ROLE_", "");});
            this.roles.forEach(role => {
              this.startLoading();
              this.userService.getUsersByRole(role.name).subscribe({
                  next: (userData: any) => {
                      if (userData.code == "200") {
                        this.users.push({ role: role, users: userData.data.content });
                        this.cdr.markForCheck();
                      }
                      this.finishLoading()
                  },
                  error: (err:HttpErrorResponse) =>{
                    errorNoti(err, this.translate)
                    this.finishLoading();
                  }
              });
            });
          }
          this.finishLoading();
        },
        error: (err:HttpErrorResponse) => {
          errorNoti(err, this.translate)
          this.finishLoading();
        }
    });
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      const sessionUser = getUser();
      if(sessionUser){
        this.canDeleteUser =
          sessionUser.role?.name === 'ROLE_ROOT' ||
          (sessionUser.role?.features?.some(
            feature => feature.name === 'DELETE_USER_MULTI'
          ) ?? false);
      }
      this.fetchRolesAndUser();
      this.fetchFeatures();
    }
  }

  handleClose(){
    this.isCreateNewUser = false;
    
    this.isRoleListOpen = false;
    this.isCreateNewRole = false;

    this.editRole = null;
    

    // this.fetchRolesAndUser()

    // this.cdr.markForCheck()
  }

  handleCloseEditUser(){
    this.editUser = null;
    this.isEditUser = false;
  }

  handleSaveEditUser(isSave:boolean){
    if(isSave){
      const message = this.translate.instant("usersManagement.User-is-saved")
      alert(message)
      this.editUser = null;
      this.isEditUser = false;
      this.fetchRolesAndUser()

      this.cdr.markForCheck()
    }
    else{
      this.editUser = null;
      this.isEditUser = false;
    }
  }
  handleSaveEditRole(isSave: boolean){
    if(isSave){
      const message = this.translate.instant("role.Role-is-updated")
      alert(message)
      this.fetchRolesAndUser();
      this.isEditRole = false;
      this.cdr.markForCheck()
    }
    else{
      this.isEditRole = false;
    }
  }

  handleSaveNewRole(isSave: boolean){
    if(isSave){
      const message = this.translate.instant("role.Role-is-created")
      alert(message)
      this.fetchRolesAndUser();
      this.isCreateNewRole = false;
      this.cdr.markForCheck()
    }
    else{
      this.isCreateNewRole = false;
    }
  }

  handleSaveNewUser(isSave: boolean){
    if(isSave){
      const message = this.translate.instant("usersManagement.User-is-created")
      alert(message)
      this.fetchRolesAndUser();
      this.isCreateNewUser = false;
      this.cdr.markForCheck()
    }
    else{
      this.isCreateNewUser = false;
    }
  }

  handleChooseRole(role: Role){
    this.editRole = role;
    this.isRoleListOpen = false;
    this.isEditRole = true;
    this.cdr.markForCheck();
  }

  getUsers(role: Role): User[] {
    const entry = this.users.find(u => u.role.name === role.name);
    return entry ? entry.users : [];
  }
}
