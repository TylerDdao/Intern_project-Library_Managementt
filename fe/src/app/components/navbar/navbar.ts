import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../services/auth-service';
import { User } from '../../models/user';

interface Page{
  name: string,
  path: string,
  authorities?: string[]
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [TranslateModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  isMobileMenuOpen = false;
  user!: User;
  fullName: string = "";
  pages: Page[] = [];
  userAuthorities: string[] = [];
  allPages: Page[] = [
    { name: 'navBar.user.Home-page', path: '/home' },
    { name: 'navBar.user.Browse-books', path: '/books', authorities: ['GET_BOOK'] },
    { name: 'navBar.user.Browse-posts', path: '/posts', authorities: ['GET_POST'] },
    { name: 'navBar.user.My-borrows', path: '/my-borrows', authorities: ['GET_BORROW'] },
    { name: 'navBar.user.My-posts', path: '/my-posts', authorities: ['GET_POST'] },
    { name: 'navBar.admin.Dashboard', path: '/dashboard', authorities: [] },
    { name: 'navBar.admin.Books-management', path: '/books-management', authorities: ['UPDATE_BOOK', 'DELETE_BOOK'] },
    { name: 'navBar.admin.Posts-management', path: '/posts-management', authorities: ['UPDATE_POST', 'DELETE_POST'] },
    { name: 'navBar.admin.Borrows-management', path: '/borrows-management', authorities: ['UPDATE_BORROW', 'DELETE_BORROW'] },
    { name: 'navBar.admin.Users-management', path: '/users-management', authorities: ['UPDATE_USER', 'DELETE_USER'] },
  ];
  
  constructor(public langService: LanguageService, private cdr: ChangeDetectorRef, @Inject(PLATFORM_ID) private platformId: Object, private authService: AuthService, private router:Router) {}

  get validateUser(): boolean {
    if (typeof localStorage === 'undefined') return false;
    return !!localStorage.getItem('token');
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  handleLogout(){
    if(isPlatformBrowser(this.platformId)){
      this.authService.logout().subscribe({
        next: (data:any) => {
          if(data.code == "200"){
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            this.router.navigate(['/login'])
          }
        }
      })
    }
  }

  ngOnInit() {
        if (isPlatformBrowser(this.platformId)) {
            const user = JSON.parse(localStorage.getItem('user') ?? '{}');
            if(user.role == "ROLE_ROOT"){
              this.pages = this.allPages;
            }
            else{
              const authorities = JSON.parse(localStorage.getItem('authorities') ?? '{}');
              this.fullName = user.fullName || '';
              this.userAuthorities = authorities || []; // assumes login response includes this

              this.pages = this.allPages.filter(page =>
                  !page.authorities || page.authorities.every(auth => this.userAuthorities.includes(auth))
              );
            }

            this.cdr.markForCheck();
        }
    }
}