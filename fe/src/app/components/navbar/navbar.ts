import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

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
  constructor(public langService: LanguageService, private cdr: ChangeDetectorRef, @Inject(PLATFORM_ID) private platformId: Object) {}

  get validateUser(): boolean {
    if (typeof localStorage === 'undefined') return false;
    return !!localStorage.getItem('token');
  }

  roleUserPages = [
    { name: 'navBar.home-page', path: '/home' }, // i18n
    { name: 'navBar.browse-books', path: '/books' }, // i18n
    { name: 'navBar.browse-posts', path: '/posts' }, // i18n
    { name: 'navBar.my-borrows', path: '/my-borrows' }, // i18n
    { name: 'navBar.my-posts', path: '/my-posts' }, // i18n
  ];

  toggleMobileMenu() {
      this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      const user = JSON.parse(localStorage.getItem('user') ?? '{}');
      this.fullName = user.fullName || '';
      this.cdr.markForCheck();
    }
  }
}