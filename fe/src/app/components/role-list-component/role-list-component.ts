import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnChanges, Output, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { Role } from '../../models/role';
import { UserService } from '../../services/user-service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { LanguageService } from '../../services/language-service/language-service';
import { TranslateModule } from '@ngx-translate/core';
import { FeatureService } from '../../services/feature-service/feature-service';
import { Page } from '../../models/page';
import { PagesComponent } from '../pages-component/pages-component';

@Component({
  selector: 'app-role-list-component',
  imports: [TranslateModule, PagesComponent],
  templateUrl: './role-list-component.html',
  styleUrl: './role-list-component.css',
})


export class RoleListComponent implements OnChanges {
  @Input() roles: Role[] = [];
  users: { role: string, users: number }[] = [];
  @Output() onClose = new EventEmitter<void>();
  @Output() onChoose = new EventEmitter<Role>();

  constructor(
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) {}

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
          this.users.push({ role: role, users: data.data.totalElements }); // check exact path
          this.cdr.markForCheck();
        }
      }
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