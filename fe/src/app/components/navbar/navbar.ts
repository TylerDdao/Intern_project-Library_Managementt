import { Component } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [TranslateModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  constructor(public langService: LanguageService) {}

  get validateUser(): boolean {
    // if (typeof localStorage === 'undefined') return false;
    // return !!localStorage.getItem('token');
    return true;
  }

  roleUserPages = [
    { name: 'navBar.home-page', path: '/home' },
    { name: 'navBar.browse-books', path: '/browse-books' },
    { name: 'navBar.browse-posts', path: '/browse-posts' },
    { name: 'navBar.my-borrows', path: '/my-borrows' },
    { name: 'navBar.my-posts', path: '/my-posts' },
  ];
}