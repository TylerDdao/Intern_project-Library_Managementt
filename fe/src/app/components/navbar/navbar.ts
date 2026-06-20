import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [TranslateModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  isMobileMenuOpen = false;
  fullName: string = "";
  pages: {name: string, path: string}[] = [];
  constructor(public langService: LanguageService, private cdr: ChangeDetectorRef, @Inject(PLATFORM_ID) private platformId: Object, private authService: AuthService, private router:Router) {}

  get validateUser(): boolean {
    if (typeof localStorage === 'undefined') return false;
    return !!localStorage.getItem('token');
  }

  roleUserPages = [
    { name: 'navBar.home-page', path: '/home' },
    { name: 'navBar.browse-books', path: '/books' },
    { name: 'navBar.browse-posts', path: '/posts' },
    { name: 'navBar.my-borrows', path: '/my-borrows' },
    { name: 'navBar.my-posts', path: '/my-posts' },
  ];

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
    if(isPlatformBrowser(this.platformId)){
      const user = JSON.parse(localStorage.getItem('user') ?? '{}');
      this.fullName = user.fullName || '';
      if(user.role == "ROLE_USER"){
        this.pages = [
          { name: 'navBar.user.Home-page', path: '/home' },
          { name: 'navBar.user.Browse-books', path: '/books' },
          { name: 'navBar.user.Browse-posts', path: '/posts' },
          { name: 'navBar.user.My-borrows', path: '/my-borrows' },
          { name: 'navBar.user.My-posts', path: '/my-posts' },
        ];
      }
      else{
        this.pages = [
          { name: 'navBar.user.Home-page', path: '/home' },
          { name: 'navBar.user.Browse-books', path: '/books' },
          { name: 'navBar.user.Browse-posts', path: '/posts' },
          { name: 'navBar.user.My-borrows', path: '/my-borrows' },
          { name: 'navBar.user.My-posts', path: '/my-posts' },

          { name: 'navBar.admin.Dashboard', path: '/dashboard' },
          { name: 'navBar.admin.Books-management', path: '/books-management' },
          { name: 'navBar.admin.Posts-management', path: '/posts-management' },
          { name: 'navBar.admin.Borrows-management', path: '/borrows-management' },
          { name: 'navBar.admin.Users-management', path: '/users-management' },
        ];
      }
      this.cdr.markForCheck();
    }
  }
}