import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { Role } from '../../models/role';
import { UserService } from '../../services/user-service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-role-list-component',
  imports: [TranslateModule, FormsModule, TranslateModule],
  templateUrl: './role-list-component.html',
  styleUrl: './role-list-component.css',
})


export class RoleListComponent implements OnChanges {
  @Input() roles: Role[] = [];
  users: { role: string, users: number }[] = [];
  @Output() onClose = new EventEmitter<void>();
  @Output() onChoose = new EventEmitter<Role>();

  query: string = ''
  resultRoles: Role[] = []

  constructor(
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) {}

  handleSearch(name: string){
    this.userService.getRole(name).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.resultRoles = data.data.content
          this.cdr.markForCheck()
        }
      },
      error(err) {console.error(err)}
    })
  }

  handleClear(){
    this.resultRoles = []
    this.query = ""
    this.cdr.markForCheck()
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['roles'] && this.roles.length > 0 && isPlatformBrowser(this.platformId)) {
      this.users = []; // reset before refetching
      this.roles.forEach(role => this.fetchUsersByRole(role.name));
    }
  }

  fetchUsersByRole(role: string) {
    this.userService.getUsersByRole(role).subscribe({
      next: (data: any) => {
        if (data.code == "200") {
          this.users.push({ role: role, users: data.data.totalElements });
          this.cdr.markForCheck();
        }
      },
      error(err) {console.error(err)}
    });
  }

  getUserCount(roleName: string): number {
    return this.users.find(u => u.role === roleName)?.users ?? 0;
  }

  close(): void {
    this.onClose.emit();
  }

  choose(role: Role): void {
    this.onChoose.emit(role);
  }
}