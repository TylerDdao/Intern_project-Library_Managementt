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

@Component({
  selector: 'app-users-management',
  imports: [NavbarComponent, UserCard, TranslateModule, NewUserForm, EditUserForm, NewRoleForm, RoleListComponent, EditRoleForm],
  templateUrl: './users-management.html',
  styleUrl: './users-management.css',
})
export class UsersManagement {
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

  
  constructor(
    private userService: UserService,
    private featureService: FeatureService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    public langService: LanguageService,
    private translate: TranslateService
    
  ){}

  handleEditUser(user: User){
    this.editUser = user;
    this.isEditUser = true;
    this.cdr.markForCheck();
  }

  fetchFeatures(){
  this.featureService.getAllFeatures().subscribe({
    next: (data: any) => {
      if(data.code == "200"){
        this.features = data.data.content
        this.cdr.markForCheck()
      }
    },
    error: (err) => console.error('features error:', err)
  })
}

  fetchRolesAndUser(){
    this.users = [];
    this.userService.getAllRoles().subscribe({
        next: (data: any) => {
          if (data.code == "200") {
            this.roles = data.data.content;
            this.roles = data.data.content.sort((a: Role, b: Role) => {
              if (a.name === 'ROLE_ROOT') return -1;
              if (b.name === 'ROLE_ROOT') return 1;
              return a.name.localeCompare(b.name);
            });
            this.roles.forEach(role => {
                this.userService.getUsersByRole(role.name).subscribe({
                    next: (userData: any) => {
                        if (userData.code == "200") {
                            this.users.push({ role: role, users: userData.data.content });
                            this.cdr.markForCheck();
                        }
                    },
                    error: (err) => console.error(err)
                });
            });
          }
        },
        error: (err) => console.error(err)
    });
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
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
      const message = this.translate.instant("userManagement.User-is-saved")
      alert(message)
      this.editUser = null;
      this.isEditUser = false;
      this.fetchRolesAndUser()

      this.cdr.markForCheck()
    }
    else{
      const message = this.translate.instant("userManagement.There-is-an-error-when-saving-user")
      alert(message)
      this.editUser = null;
      this.isEditUser = false;
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
