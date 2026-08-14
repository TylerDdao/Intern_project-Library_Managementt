import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AnnouncementService } from './services/announcement-service/announcement-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TranslateModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('library-management-fe');

  whiteList: string[] = ['/login', '/signup', '/signup/success', '/reset-password', '/test', '/unauthorized'];
  onDev: string[] = ['/logs-management', '/announcements-management']

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private announcementService: AnnouncementService
  ) {}

  ngOnInit() {
  if (!isPlatformBrowser(this.platformId)) {
    return;
  }

  // Always load announcements
  this.announcementService.loadAnnouncements().subscribe({
    next: (announcements) => {
      console.log('Announcements loaded:', announcements);
    },
    error: (err) => {
      console.error('Failed to load announcements:', err);
    }
  });

  // Authentication / routing
  const token = sessionStorage.getItem('token');
  const currentPath = window.location.pathname;

  const isWhitelisted = this.whiteList.some(path =>
    currentPath.startsWith(path)
  );

  if (token) {
    if (currentPath === '/' || currentPath === '/login') {
      this.router.navigate(['/home']);
    }

    return;
  }

  if (!isWhitelisted) {
    this.router.navigate(['/unauthorized']);
  }
}
}