import { Component, Inject, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TranslateModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('library-management-fe');

  whiteList:string[] = [
    "/login", "/signup", "/signup/success"
  ]
  
  constructor(
    private translate: TranslateService, 
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if (isPlatformBrowser(this.platformId)) {
      const token = sessionStorage.getItem('token') || localStorage.getItem('token');
      const currentPath = window.location.pathname;

      if (token) {
        if (currentPath === '/' || currentPath === '/login') {
          this.router.navigate(['/home']);
        }
      } else {
        if (!this.whiteList.includes(currentPath)) {
          this.router.navigate(['/login']);
        }
      }
    }
  }
}