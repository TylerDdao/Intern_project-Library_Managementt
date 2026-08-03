import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../services/auth-service';
import { User } from '../../models/user';
import { UserService } from '../../services/user-service/user-service';

interface Page{
  name: string,
  path: string,
  authorities?: string[],
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
  user!: User | null;
  pages: Page[] = [];
  userAuthorities: string[] = [];
  allPages: Page[] = [
    { name: 'navBar.user.Home-page', path: '/home' },
    { name: 'navBar.user.Browse-books', path: '/books', authorities: ['GET_BOOK'] },
    { name: 'navBar.user.Browse-posts', path: '/posts', authorities: ['GET_POST'] },
    { name: 'navBar.user.My-borrows', path: '/my-borrows', authorities: ['GET_BORROW'] },
    { name: 'navBar.user.My-posts', path: '/my-posts', authorities: ['GET_POST'] },
    { name: 'navBar.admin.Dashboard', path: '/dashboard', authorities: ['UPDATE_BOOK', 'UPDATE_BORROW'] },
    { name: 'navBar.admin.Books-management', path: '/books-management', authorities: ['UPDATE_BOOK', 'DELETE_BOOK'] },
    { name: 'navBar.admin.Borrows-management', path: '/borrows-management', authorities: ['GET_BORROW_MULTI', 'UPDATE_BORROW', 'DELETE_BORROW'] },
    { name: 'navBar.admin.Users-management', path: '/users-management', authorities: ['UPDATE_USER', 'DELETE_USER'] },
  ];
  
  constructor(
    public langService: LanguageService, 
    private cdr: ChangeDetectorRef, 
    @Inject(PLATFORM_ID) private platformId: Object, 
    private authService: AuthService, 
    private router:Router,
    private userService:UserService) {}

  get validateUser(): boolean {
    if (typeof sessionStorage === 'undefined') return false;
    return !!sessionStorage.getItem('token');
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  handleLogout(){
    if(isPlatformBrowser(this.platformId)){
      this.authService.logout().subscribe({
        next: (data:any) => {
          if(data.code == "200"){
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('user')
            sessionStorage.removeItem('authorities')
            this.router.navigate(['/login'])
          }
        }
      })
    }
  }

  ngOnInit() {
    if (!isPlatformBrowser(this.platformId)) return;

    this.userService.user$.subscribe(user => {
      this.user = user;

      if (!user) {
        this.pages = [];
        return;
      }

      if (user.role?.name === 'ROLE_ROOT') {
        this.pages = this.allPages;
      } else {
        const authorities = JSON.parse(
          sessionStorage.getItem('authorities') ?? '[]'
        );

        this.userAuthorities = authorities;

        this.pages = this.allPages.filter(
          page =>
            !page.authorities ||
            page.authorities.some(auth => authorities.includes(auth))
        );
      }

      this.cdr.markForCheck();
    });
  }
}