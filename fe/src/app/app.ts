import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TranslateModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('library-management-fe');

  whiteList: string[] = ['/login', '/signup', '/signup/success', '/reset-password', '/test'];

  constructor(
    private translate: TranslateService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      
      const token = sessionStorage.getItem('token');
      const currentPath = window.location.pathname;

      if (token) {
        if (currentPath === '/' || currentPath === '/login') {
          this.router.navigate(['/home']);
        }
      } else {
        const isWhitelisted = this.whiteList.some(path => currentPath.startsWith(path));
        if (!isWhitelisted) {
          console.log("WHITE LIST")
          this.router.navigate(['/login']);
        }
      }
    }
  }
}