import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { UserCard } from '../../components/user-card/user-card';
import { User } from '../../models/user';
import { UserService } from '../../services/user-service/user-service';
import { Role } from '../../models/role';
import { isPlatformBrowser } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { NewUserForm } from '../../forms/new-user-form/new-user-form';
import { EditUserForm } from '../../forms/edit-user-form/edit-user-form';

@Component({
  selector: 'app-users-management',
  imports: [NavbarComponent, UserCard, TranslateModule, NewUserForm, EditUserForm],
  templateUrl: './users-management.html',
  styleUrl: './users-management.css',
})
export class UsersManagement {
  roles: Role[] = [];

  users: { role: Role, users: User[] }[] = [];

  isCreateNewUser: boolean = false;
  isEditUser: boolean = false;
  editUser: User | null = null;

  
  constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    public langService: LanguageService,
    
  ){}

  handleEditUser(user: User){
    this.editUser = user;
    this.isEditUser = true;
    this.cdr.markForCheck();
  }

  fetchRoles(){
    this.userService.getAllRoles().subscribe({
        next: (data: any) => {
          if (data.code == "200") {
            this.roles = data.data.content;            
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
      this.fetchRoles();
    }
  }

  handleCloseCreateUserForm(){
    this.isCreateNewUser = false;
    this.cdr.markForCheck()
  }

  handleCloseEditUserForm() {
    this.isEditUser = false;
    this.editUser = null;
    this.cdr.markForCheck();
  }

  getUsers(role: Role): User[] {
    const entry = this.users.find(u => u.role.name === role.name);
    return entry ? entry.users : [];
  }
}
